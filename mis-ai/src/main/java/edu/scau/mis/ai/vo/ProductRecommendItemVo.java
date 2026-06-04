package edu.scau.mis.ai.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRecommendItemVo {

    private String productId;

    private String productName;

    private BigDecimal price;

    private String imageUrl;

    /**
     * 推荐理由
     */
    private String reason;
}