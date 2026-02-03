package edu.scau.mis.cart.mq;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import edu.scau.mis.cart.config.RabbitConfig;
import edu.scau.mis.common.domain.OmsCartItem;
import edu.scau.mis.cart.mapper.CartItemMapper;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RabbitListener(queues = RabbitConfig.CART_SYNC_QUEUE)
public class CartSyncListener {

    @Autowired
    private CartItemMapper cartItemMapper;

    @RabbitHandler
    public void process(OmsCartItem item) {
        System.out.println("📥 MQ 收到购物车同步数据: " + item);

        // 使用 "存在即更新，不存在即插入" 逻辑
        // 1. 根据 userId + productId 查库
        LambdaQueryWrapper<OmsCartItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OmsCartItem::getMemberId, item.getMemberId())
               .eq(OmsCartItem::getProductId, item.getProductId());
        
        OmsCartItem dbItem = cartItemMapper.selectOne(wrapper);

        if (dbItem != null) {
            // 更新
            dbItem.setQuantity(item.getQuantity());
            dbItem.setModifyDate(new Date());
            cartItemMapper.updateById(dbItem);
        } else {
            // 插入
            cartItemMapper.insert(item);
        }
    }
}