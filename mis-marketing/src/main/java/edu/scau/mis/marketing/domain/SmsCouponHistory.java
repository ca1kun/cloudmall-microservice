package edu.scau.mis.marketing.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("sms_coupon_history")
public class SmsCouponHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long couponId; // 优惠券ID

    private Long memberId; // 会员ID

    private String memberName; // 会员名字

    private Date createTime; // 领取时间

    private Integer useStatus; // 使用状态 0->未使用；1->已使用；2->已过期

    private Date useTime;
    private Long orderId;
    private String orderSn; // 如果加了的话
}