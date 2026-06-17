package edu.scau.mis.pay.service.impl;

import cn.hutool.json.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.common.domain.OmsOrder;
import edu.scau.mis.common.exception.ServiceException;
import edu.scau.mis.common.mapper.OmsOrderMapper;
import edu.scau.mis.pay.config.AlipayConfig;
import edu.scau.mis.pay.domain.PaymentInfo;
import edu.scau.mis.pay.feign.RemoteOrderService;
import edu.scau.mis.pay.mapper.PaymentInfoMapper;
import edu.scau.mis.pay.service.PayService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class PayServiceImpl implements PayService {

    private static final int ORDER_STATUS_PENDING_PAYMENT = 0;
    private static final int ORDER_STATUS_PAID = 1;
    private static final String PAYMENT_STATUS_PENDING = "PENDING";
    private static final String PAYMENT_STATUS_SUCCESS = "SUCCESS";
    private static final String TRADE_SUCCESS = "TRADE_SUCCESS";
    private static final String TRADE_FINISHED = "TRADE_FINISHED";

    private final AlipayClient alipayClient;
    private final AlipayConfig alipayConfig;
    private final RemoteOrderService remoteOrderService;
    private final PaymentInfoMapper paymentMapper;
    private final OmsOrderMapper orderMapper;

    public PayServiceImpl(AlipayClient alipayClient,
                          AlipayConfig alipayConfig,
                          RemoteOrderService remoteOrderService,
                          PaymentInfoMapper paymentMapper,
                          OmsOrderMapper orderMapper) {
        this.alipayClient = alipayClient;
        this.alipayConfig = alipayConfig;
        this.remoteOrderService = remoteOrderService;
        this.paymentMapper = paymentMapper;
        this.orderMapper = orderMapper;
    }

    @Override
    public String pay(Long orderId) {
        ApiResult<OmsOrder> orderResult = remoteOrderService.getOrderById(orderId);
        if (orderResult == null || orderResult.getData() == null) {
            throw new ServiceException("远程调用失败或订单不存在: orderId=" + orderId);
        }

        OmsOrder order = orderResult.getData();
        if (order.getStatus() == null || order.getStatus() != ORDER_STATUS_PENDING_PAYMENT) {
            throw new ServiceException("订单状态异常: status=" + order.getStatus());
        }

        PaymentInfo info = new PaymentInfo();
        info.setOrderId(orderId);
        info.setOrderSn(order.getOrderSn());
        info.setTotalAmount(order.getPayAmount());
        info.setSubject("SCAU商城-订单" + order.getOrderSn());
        info.setPaymentStatus(PAYMENT_STATUS_PENDING);
        info.setCreateTime(new Date());
        paymentMapper.insert(info);

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setReturnUrl(alipayConfig.getReturnUrl());

        JSONObject bizContent = new JSONObject();
        bizContent.set("out_trade_no", order.getOrderSn());
        bizContent.set("total_amount", order.getPayAmount());
        bizContent.set("subject", "SCAU商城-订单" + order.getOrderSn());
        bizContent.set("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());

        try {
            return alipayClient.pageExecute(request).getBody();
        } catch (AlipayApiException e) {
            throw new ServiceException("调用支付宝失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> syncAlipayReturn(String orderSn, String alipayTradeNo) {
        if (orderSn == null || orderSn.trim().isEmpty()) {
            throw new ServiceException("缺少支付宝订单号 out_trade_no");
        }

        String trimmedOrderSn = orderSn.trim();
        AlipayTradeQueryResponse response = queryAlipayTrade(trimmedOrderSn, alipayTradeNo);
        String tradeStatus = response.getTradeStatus();
        if (!TRADE_SUCCESS.equals(tradeStatus) && !TRADE_FINISHED.equals(tradeStatus)) {
            throw new ServiceException("支付宝交易未成功: tradeStatus=" + tradeStatus);
        }

        PaymentInfo paymentInfo = findLatestPayment(trimmedOrderSn);
        if (paymentInfo == null) {
            throw new ServiceException("本地支付流水不存在: orderSn=" + trimmedOrderSn);
        }

        if (!PAYMENT_STATUS_SUCCESS.equals(paymentInfo.getPaymentStatus())) {
            paymentInfo.setPaymentStatus(PAYMENT_STATUS_SUCCESS);
            paymentInfo.setAlipayTradeNo(response.getTradeNo());
            paymentInfo.setCallbackTime(new Date());
            paymentMapper.updateById(paymentInfo);
        }

        LambdaUpdateWrapper<OmsOrder> orderWrapper = new LambdaUpdateWrapper<>();
        orderWrapper.eq(OmsOrder::getId, paymentInfo.getOrderId())
                .eq(OmsOrder::getStatus, ORDER_STATUS_PENDING_PAYMENT)
                .set(OmsOrder::getStatus, ORDER_STATUS_PAID);
        int updated = orderMapper.update(null, orderWrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", paymentInfo.getOrderId());
        result.put("orderSn", trimmedOrderSn);
        result.put("alipayTradeNo", response.getTradeNo());
        result.put("tradeStatus", tradeStatus);
        result.put("orderUpdated", updated > 0);
        return result;
    }

    private AlipayTradeQueryResponse queryAlipayTrade(String orderSn, String alipayTradeNo) {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.set("out_trade_no", orderSn);
        if (alipayTradeNo != null && !alipayTradeNo.trim().isEmpty()) {
            bizContent.set("trade_no", alipayTradeNo.trim());
        }
        request.setBizContent(bizContent.toString());

        try {
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            if (response == null || !response.isSuccess()) {
                throw new ServiceException("支付宝交易查询失败: " + (response == null ? "no response" : response.getSubMsg()));
            }
            return response;
        } catch (AlipayApiException e) {
            throw new ServiceException("支付宝交易查询异常: " + e.getMessage());
        }
    }

    private PaymentInfo findLatestPayment(String orderSn) {
        LambdaQueryWrapper<PaymentInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentInfo::getOrderSn, orderSn)
                .orderByDesc(PaymentInfo::getId)
                .last("LIMIT 1");
        return paymentMapper.selectOne(wrapper);
    }
}
