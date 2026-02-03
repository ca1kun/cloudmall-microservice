package edu.scau.mis.pay.service.impl;

import cn.hutool.json.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.common.domain.OmsOrder;
import edu.scau.mis.common.exception.ServiceException;
import edu.scau.mis.pay.config.AlipayConfig;
import edu.scau.mis.pay.domain.PaymentInfo;
import edu.scau.mis.pay.feign.RemoteOrderService;
import edu.scau.mis.pay.mapper.PaymentInfoMapper;
import edu.scau.mis.pay.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PayServiceImpl implements PayService {

    private final AlipayClient alipayClient;
    private final AlipayConfig alipayConfig;
    private final RemoteOrderService remoteOrderService; // FeignClient
    private final PaymentInfoMapper paymentMapper;

    public PayServiceImpl(AlipayClient alipayClient, AlipayConfig alipayConfig, RemoteOrderService remoteOrderService, PaymentInfoMapper paymentMapper) {
        this.alipayClient = alipayClient;
        this.alipayConfig = alipayConfig;
        this.remoteOrderService = remoteOrderService;
        this.paymentMapper = paymentMapper;
    }

    @Override
    public String pay(Long orderId) {
        // 1. 远程查询订单详情
        ApiResult<OmsOrder> orderResult = remoteOrderService.getOrderById(orderId);
        System.out.println("PayService: 查订单结果=" + orderResult);
        if (orderResult != null) {
            System.out.println("PayService: Data=" + orderResult.getData());
        }

        if (orderResult == null || orderResult.getData() == null) {
            // 👇 明确一下是哪一步空了
            throw new ServiceException("远程调用失败或订单不存在 (orderResult=" + orderResult + ")");
        }
        OmsOrder order = orderResult.getData();
        System.out.println("PayService: 订单状态=" + order.getStatus()); // 👈 打印状态

        // 2. 检查状态
        if (order.getStatus() != 0) { // 0=待付款
            throw new ServiceException("订单状态异常: status=" + order.getStatus());
        }
        // 2.5 生成支付流水 (PENDING)
        PaymentInfo info = new PaymentInfo();
        info.setOrderId(orderId);
        info.setOrderSn(order.getOrderSn());
        info.setTotalAmount(order.getPayAmount());
        info.setSubject("SCAU商城-订单" + order.getOrderSn());
        info.setPaymentStatus("PENDING"); // 待支付
        info.setCreateTime(new Date());

        paymentMapper.insert(info);
        System.out.println("✅ 支付流水已创建，ID: " + info.getId());

        // 3. 构造支付宝请求
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setReturnUrl(alipayConfig.getReturnUrl());
        // request.setNotifyUrl(alipayConfig.getNotifyUrl()); // 本地无法接收，先注释

        JSONObject bizContent = new JSONObject();
        bizContent.set("out_trade_no", order.getOrderSn()); // 订单号
        bizContent.set("total_amount", order.getPayAmount()); // 金额
        bizContent.set("subject", "SCAU商城-订单" + order.getOrderSn());
        bizContent.set("product_code", "FAST_INSTANT_TRADE_PAY");

        request.setBizContent(bizContent.toString());

        try {
            // 4. 生成表单 HTML
            return alipayClient.pageExecute(request).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
            throw new ServiceException("调用支付宝失败: " + e.getMessage());
        }
    }
}