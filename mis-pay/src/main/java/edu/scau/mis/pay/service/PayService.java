package edu.scau.mis.pay.service;

import java.util.Map;

public interface  PayService {
    String pay(Long orderId);

    Map<String, Object> syncAlipayReturn(String orderSn, String alipayTradeNo);
}
