package edu.scau.mis.ai.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRecommendItemDTO {
    private Long productId;
    private String productName;
    private BigDecimal price;
    private String imageUrl;
    private String reason;
    private Integer stock;
}
