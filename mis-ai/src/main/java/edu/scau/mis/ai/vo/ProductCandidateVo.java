package edu.scau.mis.ai.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductCandidateVo {

    private String productId;

    private String productName;

    private String categoryName;

    private BigDecimal price;

    private Integer stock;

    /**
     * 卖点，例如：续航长、降噪、适合学生
     */
    private String sellingPoint;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品图片
     */
    private String imageUrl;
}