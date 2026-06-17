package edu.scau.mis.ai.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CouponQueryDTO {
    private Long couponId;
    private String couponName;
    private BigDecimal amount;
    private Integer useStatus;
    private String endTime;
}
