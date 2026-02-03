package edu.scau.mis.order.listen;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.common.dto.StockLockDTO;
import edu.scau.mis.common.domain.OmsOrder;
import edu.scau.mis.order.domain.OmsOrderItem;
import edu.scau.mis.order.feign.RemoteProductService;
import edu.scau.mis.order.mapper.OmsOrderItemMapper;
import edu.scau.mis.common.mapper.OmsOrderMapper;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Component
@RabbitListener(queues = "order.release.order.queue") // 监听死信队列
public class OrderTimeOutListener {

    @Autowired
    private OmsOrderMapper orderMapper;

    @Autowired
    private OmsOrderItemMapper orderItemMapper; // 需要查订单买了啥

    @Autowired
    private RemoteProductService remoteProductService; // Feign 调商品服务

    @RabbitHandler
    public void handleOrderRelease(Long orderId) {
        System.out.println("⏰ [订单超时] 收到关单消息，检查订单ID: " + orderId);

        // 1. 查订单当前状态
        OmsOrder order = orderMapper.selectById(orderId);

        // ⚠️ 关键判断：只有状态是 0 (待付款) 的时候才关单
        // 如果用户已经付过款了 (status=1)，或者已经取消了 (status=4)，就不要动了
        if (order != null && order.getStatus() == 0) {

            System.out.println("❌ 订单未支付，执行自动关闭逻辑...");

            // 2. 修改订单状态为 4 (已关闭)
            order.setStatus(4);
            orderMapper.updateById(order);

            // 3. 准备回滚库存
            // 先查出这个订单买了哪些商品
            LambdaQueryWrapper<OmsOrderItem> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(OmsOrderItem::getOrderId, orderId);
            List<OmsOrderItem> orderItems = orderItemMapper.selectList(wrapper);

            // 组装参数
            List<StockLockDTO> stockLockList = orderItems.stream().map(item -> {
                StockLockDTO dto = new StockLockDTO();
                dto.setProductId(item.getProductId());
                dto.setCount(item.getProductQuantity());
                return dto;
            }).collect(Collectors.toList());
            System.out.println("发送回滚请求，参数: " + stockLockList); // 👈 打印这个

            // 4. 远程调用 mis-web 恢复库存
            if (!stockLockList.isEmpty()) {
                ApiResult<String> result = remoteProductService.unlockStock(stockLockList);
                if (result.getCode() == 200) {
                    System.out.println("✅ 库存已成功回滚");
                } else {
                    System.err.println("⚠️ 库存回滚失败，需人工介入: " + result.getMessage());
                }
            }
        } else {
            System.out.println("✅ 订单状态正常(已支付或已关闭)，无需处理");
        }
    }
}