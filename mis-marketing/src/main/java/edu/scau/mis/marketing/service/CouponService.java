package edu.scau.mis.marketing.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import edu.scau.mis.common.exception.ServiceException;

import edu.scau.mis.marketing.domain.CouponHistoryDetail;
import edu.scau.mis.common.domain.SmsCoupon;
import edu.scau.mis.marketing.domain.SmsCouponHistory;
import edu.scau.mis.marketing.mapper.SmsCouponHistoryMapper;
import edu.scau.mis.marketing.mapper.SmsCouponMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CouponService{

    @Autowired
    private SmsCouponMapper couponMapper; // 需要查数据库
    @Autowired
    private SmsCouponHistoryMapper historyMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate; // 使用 String 类型的 Template 操作 Lua
    @Autowired
    private DefaultRedisScript<Long> seckillScript;
    @Autowired
    private RabbitTemplate rabbitTemplate; // 需要引入 RabbitMQ 依赖

    /**
     * 库存预热：把数据库的库存同步到 Redis
     * @param couponId 优惠券ID
     */
    public void preHeat(Long couponId) {
        // 1. 从数据库查询优惠券信息
        SmsCoupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new ServiceException("优惠券不存在");
        }

        // 2. 获取数据库里的库存数量
        Integer stock = coupon.getCount();
        System.out.println("🔥 开始预热，数据库库存为: " + stock);

        // 3. 存入 Redis (Key: seckill:stock:ID)
        String stockKey = "seckill:stock:" + couponId;
        stringRedisTemplate.opsForValue().set(stockKey, String.valueOf(stock));

        // 4. 为了测试方便，顺便把“已抢购用户名单”清空
        // 这样你可以反复测试，不用每次都去数据库删记录
        String historyKey = "seckill:users:" + couponId;
        stringRedisTemplate.delete(historyKey);

        System.out.println("✅ 预热完成！Redis Key [" + stockKey + "] 已设置为: " + stock);
    }

    // 2. 秒杀核心接口
    public void secKillCoupon(Long couponId, Long userId, String username) {
        // 构造 Key
        String stockKey = "seckill:stock:" + couponId;
        String userHistoryKey = "seckill:users:" + couponId;

        // 执行 Lua 脚本 (原子操作)
        // 参数1: 脚本对象, 参数2: KEYS列表, 参数3: ARGV列表
        Long result = stringRedisTemplate.execute(
                seckillScript,
                Arrays.asList(stockKey, userHistoryKey),
                userId.toString()
        );

        if (result == -1) {
            throw new ServiceException("您已经领过券了");
        } else if (result == -2) {
            throw new ServiceException("手慢了，已抢光");
        } else if (result == 0) {
            // ✅ Redis 抢购成功！
            // 此时 Redis 数据已经变了，库存-1，用户记录+1。
            // 接下来：发送消息给 MQ，让数据库慢慢去同步，不要卡住用户

            Map<String, Object> msg = new HashMap<>();
            msg.put("couponId", couponId);
            msg.put("userId", userId);
            msg.put("username", username);

            rabbitTemplate.convertAndSend("coupon.queue", msg);

            System.out.println("✅ 用户 " + userId + " Redis抢购成功，已发送MQ");
        }
    }
    public List<SmsCoupon> list(LambdaQueryWrapper<SmsCoupon> queryWrapper) {
        return couponMapper.selectList(queryWrapper);
    }

    // 👇 新增这个方法，一次性把历史和详情都查出来
    public List<CouponHistoryDetail> listMyCoupons(Long userId) {
        List<SmsCouponHistory> historyList = listHistory(userId);

        return historyList.stream().map(h -> {
            SmsCoupon c = couponMapper.selectById(h.getCouponId());

            CouponHistoryDetail dto = new CouponHistoryDetail();

            // 1. 这一步已经把 id, useStatus, couponId 从 h 拷贝给 dto 了
            BeanUtils.copyProperties(h, dto);

            // 2. 补全 coupon 信息
            if (c != null) {
                // dto.setCouponId(c.getId()); // 其实这行也不用，copyProperties 已经拷了
                dto.setName(c.getName());
                dto.setAmount(c.getAmount());
                dto.setMinPoint(c.getMinPoint());
                dto.setStartTime(c.getStartTime());
                dto.setEndTime(c.getEndTime());

            }
            return dto;
        }).collect(Collectors.toList());
    }
    /**
     * 查询某用户的领取历史
     */
    public List<SmsCouponHistory> listHistory(Long userId) {
        LambdaQueryWrapper<SmsCouponHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SmsCouponHistory::getMemberId, userId);
        wrapper.orderByDesc(SmsCouponHistory::getCreateTime); // 按时间倒序
        return historyMapper.selectList(wrapper);
    }


    public SmsCoupon getById(Long couponId) {
        return this.couponMapper.selectById(couponId);
    }

    public void useCoupon(Long couponId, Long userId, Long orderId) {
        LambdaQueryWrapper<SmsCouponHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SmsCouponHistory::getCouponId, couponId);
        wrapper.eq(SmsCouponHistory::getMemberId, userId);
        wrapper.eq(SmsCouponHistory::getUseStatus, 0); // 必须是未使用

        // 只取第一张
        SmsCouponHistory history = historyMapper.selectOne(wrapper.last("LIMIT 1"));

        if (history == null) {
            throw new ServiceException("优惠券不可用或不存在");
        }

        // 更新状态
        history.setUseStatus(1); // 已使用
        history.setUseTime(new Date());
        history.setOrderId(orderId); // 记录是哪个订单用的
        historyMapper.updateById(history);
    }

    public boolean save(SmsCoupon coupon) {
        try {
            int result = couponMapper.insert(coupon);
            return result > 0;
        } catch (Exception e) {
            throw new ServiceException("保存优惠券失败: " + e.getMessage());
        }
    }

    public void page(IPage<SmsCoupon> page, LambdaQueryWrapper<SmsCoupon> queryWrapper) {
        couponMapper.selectPage(page, queryWrapper);
    }

}