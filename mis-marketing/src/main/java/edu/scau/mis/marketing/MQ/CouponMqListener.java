package edu.scau.mis.marketing.MQ;

import edu.scau.mis.marketing.config.RabbitConfig;
import edu.scau.mis.marketing.domain.SmsCouponHistory;
import edu.scau.mis.marketing.mapper.SmsCouponHistoryMapper;
import edu.scau.mis.marketing.mapper.SmsCouponMapper;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component
@RabbitListener(queues = RabbitConfig.COUPON_QUEUE)
public class CouponMqListener {

    @Autowired
    private SmsCouponMapper couponMapper;

    @Autowired
    private SmsCouponHistoryMapper historyMapper;

    @RabbitHandler
    @Transactional(rollbackFor = Exception.class)
    public void process(CouponSeckillMessage msg) {
        Long couponId = msg.getCouponId();
        Long userId = msg.getUserId();
        String username = msg.getUsername();

        SmsCouponHistory history = new SmsCouponHistory();
        history.setCouponId(couponId);
        history.setMemberId(userId);
        history.setMemberName(username);
        history.setCreateTime(new Date());
        history.setUseStatus(0);

        try {
            historyMapper.insert(history);
        } catch (DuplicateKeyException e) {
            // MQ 重复消费，直接忽略，不再扣库存
            return;
        }

        int rows = couponMapper.decreaseStock(couponId);

        if (rows <= 0) {
            throw new RuntimeException("数据库优惠券库存扣减失败，couponId=" + couponId);
        }
    }
}