package edu.scau.mis.order.controller;


import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.common.domain.OmsOrder;
import edu.scau.mis.order.dto.OrderParam;
import edu.scau.mis.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public ApiResult<Map<String, Object>> createOrder(@RequestBody OrderParam orderParam) {
        Map<String, Object> result = orderService.createOrder(orderParam);
        return ApiResult.success("下单成功", result);
    }
    /**
     * 根据 ID 获取订单详情
     */
    @GetMapping("/{id}")
    public ApiResult<OmsOrder> getOrderById(@PathVariable("id") Long id) {
        OmsOrder order = orderService.getOrderById(id);
        if (order != null) {
            return ApiResult.success(order);
        }
        return ApiResult.error("订单不存在");
    }

}