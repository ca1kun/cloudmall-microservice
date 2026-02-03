package edu.scau.mis.pay.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("oms_payment_info")
public class PaymentInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    // 订单编号
    private String orderSn;

    // 订单ID
    private Long orderId;

    // 支付宝交易号
    private String alipayTradeNo;

    // 支付金额
    private BigDecimal totalAmount;

    // 交易内容
    private String subject;

    // 支付状态 (PENDING / SUCCESS)
    private String paymentStatus;

    // 创建时间
    private Date createTime;

    // 回调时间
    private Date callbackTime;
}