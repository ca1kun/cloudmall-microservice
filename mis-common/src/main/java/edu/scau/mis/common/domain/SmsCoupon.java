package edu.scau.mis.common.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("sms_coupon")
public class SmsCoupon implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name; // 优惠券名称

    private Integer count; // 剩余数量

    private BigDecimal amount; // 金额

    private Date startTime; // 开始时间

    private Date endTime; // 结束时间

    private BigDecimal minPoint; // 使用门槛

    private Integer perLimit; // 每人限领张数
}