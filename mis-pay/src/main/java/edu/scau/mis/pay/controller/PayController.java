package edu.scau.mis.pay.controller;

import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.common.domain.OmsOrder;
import edu.scau.mis.common.mapper.OmsOrderMapper;
import edu.scau.mis.pay.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Autowired
    private PayService payService;

    @Autowired
    private OmsOrderMapper orderMapper;

    @GetMapping("/alipay")
    public ApiResult<String> alipay(@RequestParam Long orderId) {
        // 返回的是一段 HTML 代码（包含自动提交的 Form 表单）
        String formHtml = payService.pay(orderId);
        return ApiResult.success(formHtml);
    }
    /**
     * 根据 ID 获取订单详情 (供 mis-pay 调用)
     */
    @GetMapping("/{id}")
    public ApiResult<OmsOrder> getOrderById(@PathVariable("id") Long id) {
        OmsOrder order = orderMapper.selectById(id);
        return ApiResult.success(order);
    }
}