package edu.scau.mis.order.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.common.domain.OmsOrder;
import edu.scau.mis.common.mapper.OmsOrderMapper;
import edu.scau.mis.order.domain.OmsOrderItem;
import edu.scau.mis.order.mapper.OmsOrderItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/order/ai")
public class OrderAiController {

    @Autowired
    private OmsOrderMapper orderMapper;
    @Autowired
    private OmsOrderItemMapper orderItemMapper;

    private static final DateTimeFormatter SN_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    @PostMapping("/create")
    public ApiResult<Map<String, Object>> create(@RequestParam Long memberId,
                                                  @RequestParam Long productId,
                                                  @RequestParam(defaultValue = "1") Integer quantity) {
        String orderSn = LocalDateTime.now().format(SN_FMT)
                + ThreadLocalRandom.current().nextInt(1000, 10000);

        OmsOrder order = new OmsOrder();
        order.setMemberId(memberId);
        order.setOrderSn(orderSn);
        order.setTotalAmount(BigDecimal.ZERO);
        order.setPayAmount(BigDecimal.ZERO);
        order.setStatus(0);
        order.setCreateTime(LocalDateTime.now());
        orderMapper.insert(order);

        OmsOrderItem item = new OmsOrderItem();
        item.setOrderId(order.getId());
        item.setOrderSn(orderSn);
        item.setProductId(productId);
        item.setProductQuantity(quantity);
        orderItemMapper.insert(item);

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", order.getId());
        result.put("orderSn", orderSn);
        return ApiResult.success("下单成功", result);
    }

    @PostMapping("/cancel")
    public ApiResult<String> cancel(@RequestParam Long memberId, @RequestParam String orderSn) {
        OmsOrder order = orderMapper.selectOne(new LambdaQueryWrapper<OmsOrder>()
                .eq(OmsOrder::getMemberId, memberId)
                .eq(OmsOrder::getOrderSn, orderSn));
        if (order == null) return ApiResult.error("订单不存在");
        order.setStatus(4);
        orderMapper.updateById(order);
        return ApiResult.success("订单已取消");
    }
}
