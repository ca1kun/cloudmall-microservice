package edu.scau.mis.order.dto;

import lombok.Data;

@Data
public class OrderParam {
    // 收货地址ID (暂时可以模拟填 1)
    private Long addressId;
    // 支付方式 (1:支付宝 2:微信)
    private Integer payType;
    // 订单备注
    private String note;

    private Long couponId;
}