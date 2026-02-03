package edu.scau.mis.marketing.MQ;

import edu.scau.mis.marketing.domain.SmsCouponHistory;
import edu.scau.mis.marketing.mapper.SmsCouponHistoryMapper;
import edu.scau.mis.marketing.mapper.SmsCouponMapper;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
@RabbitListener(queues = "coupon.queue") // 确保你在 RabbitMQ 里建了这个队列
public class CouponMqListener {

    @Autowired
    private SmsCouponMapper couponMapper;
    @Autowired
    private SmsCouponHistoryMapper historyMapper;

    @RabbitHandler
    public void process(Map<String, Object> msg) {
        Long couponId = Long.valueOf(msg.get("couponId").toString());
        Long userId = Long.valueOf(msg.get("userId").toString());
        String username = (String) msg.get("username");

        System.out.println("📥 MQ收到消息，开始同步数据库: User=" + userId);

        // 1. 扣减数据库库存 (这里其实不需要判断 >0 了，因为 Redis 已经挡住了，但保留也无妨)
        // 建议直接用 SQL: UPDATE ... SET count = count - 1 WHERE id = ?
        couponMapper.decreaseStock(couponId);

        // 2. 插入领取记录
        SmsCouponHistory history = new SmsCouponHistory();
        history.setCouponId(couponId);
        history.setMemberId(userId);
        history.setMemberName(username);
        history.setCreateTime(new Date());
        try {
            historyMapper.insert(history);
        } catch (DuplicateKeyException e) {
            // 幂等性处理：如果 MQ 重复消费，数据库唯一索引会报错，忽略即可
            System.out.println("⚠️ 数据库记录已存在，忽略");
        }
    }
}