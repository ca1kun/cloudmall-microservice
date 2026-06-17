package edu.scau.mis.marketing.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import edu.scau.mis.common.domain.SmsCoupon;
import edu.scau.mis.common.exception.ServiceException;
import edu.scau.mis.marketing.MQ.CouponSeckillMessage;
import edu.scau.mis.marketing.domain.CouponHistoryDetail;
import edu.scau.mis.marketing.domain.SmsCouponHistory;
import edu.scau.mis.marketing.mapper.SmsCouponHistoryMapper;
import edu.scau.mis.marketing.mapper.SmsCouponMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static edu.scau.mis.marketing.config.RabbitConfig.COUPON_EXCHANGE;
import static edu.scau.mis.marketing.config.RabbitConfig.COUPON_ROUTING_KEY;

@Service
public class CouponService {

    public static final String SECKILL_SUCCESS_MSG = "抢券请求已提交，处理中";

    @Autowired
    private SmsCouponMapper couponMapper;

    @Autowired
    private SmsCouponHistoryMapper historyMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private DefaultRedisScript<Long> seckillScript;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 库存预热：把数据库的库存同步到 Redis
     */
    public void preHeat(Long couponId) {
        SmsCoupon coupon = couponMapper.selectById(couponId);
        if (coupon == null) {
            throw new ServiceException("优惠券不存在");
        }

        Integer stock = coupon.getCount();

        String stockKey = "seckill:stock:" + couponId;
        String historyKey = "seckill:users:" + couponId;

        stringRedisTemplate.opsForValue().set(stockKey, String.valueOf(stock));
        stringRedisTemplate.delete(historyKey);
    }

    /**
     * 秒杀核心接口：只做 Redis Lua + MQ，不能同步写 MySQL。
     */
    public String secKillCoupon(Long couponId, Long userId, String username) {
        String stockKey = "seckill:stock:" + couponId;
        String userHistoryKey = "seckill:users:" + couponId;

        Long result = stringRedisTemplate.execute(
                seckillScript,
                Arrays.asList(stockKey, userHistoryKey),
                userId.toString()
        );

        if (result == null) {
            return "系统繁忙，请稍后再试";
        }

        if (result == -1) {
            return "您已经领过券了";
        }

        if (result == -2) {
            return "手慢了，已抢光";
        }

        CouponSeckillMessage msg = new CouponSeckillMessage(couponId, userId, username);

        rabbitTemplate.convertAndSend(COUPON_EXCHANGE, COUPON_ROUTING_KEY, msg);

        return SECKILL_SUCCESS_MSG;
    }

    public List<SmsCoupon> list(LambdaQueryWrapper<SmsCoupon> queryWrapper) {
        return couponMapper.selectList(queryWrapper);
    }

    public List<CouponHistoryDetail> listMyCoupons(Long userId) {
        List<SmsCouponHistory> historyList = listHistory(userId);

        return historyList.stream().map(h -> {
            SmsCoupon c = couponMapper.selectById(h.getCouponId());

            CouponHistoryDetail dto = new CouponHistoryDetail();
            BeanUtils.copyProperties(h, dto);

            if (c != null) {
                dto.setName(c.getName());
                dto.setAmount(c.getAmount());
                dto.setMinPoint(c.getMinPoint());
                dto.setStartTime(c.getStartTime());
                dto.setEndTime(c.getEndTime());
            }

            return dto;
        }).collect(Collectors.toList());
    }

    public List<SmsCouponHistory> listHistory(Long userId) {
        LambdaQueryWrapper<SmsCouponHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SmsCouponHistory::getMemberId, userId);
        wrapper.orderByDesc(SmsCouponHistory::getCreateTime);
        return historyMapper.selectList(wrapper);
    }

    public SmsCoupon getById(Long couponId) {
        return couponMapper.selectById(couponId);
    }

    public void useCoupon(Long couponId, Long userId, Long orderId) {
        LambdaQueryWrapper<SmsCouponHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SmsCouponHistory::getCouponId, couponId);
        wrapper.eq(SmsCouponHistory::getMemberId, userId);
        wrapper.eq(SmsCouponHistory::getUseStatus, 0);

        SmsCouponHistory history = historyMapper.selectOne(wrapper.last("LIMIT 1"));

        if (history == null) {
            throw new ServiceException("优惠券不可用或不存在");
        }

        history.setUseStatus(1);
        history.setUseTime(new Date());
        history.setOrderId(orderId);
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