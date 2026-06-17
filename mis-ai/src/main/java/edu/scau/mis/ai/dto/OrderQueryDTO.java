package edu.scau.mis.ai.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderQueryDTO {
    private Long orderId;
    private String orderSn;
    private Integer status;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private List<String> productNames;
}
