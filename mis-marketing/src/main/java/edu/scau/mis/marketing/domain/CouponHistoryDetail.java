package edu.scau.mis.marketing.domain;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CouponHistoryDetail {
    private Long id; // 领取记录ID
    private Long couponId;
    private String name;
    private BigDecimal amount;
    private BigDecimal minPoint;
    private Date startTime;
    private Date endTime;
    private Integer useStatus; // 0未使用 1已使用
}