package edu.scau.mis.ai.dto;

import lombok.Data;

@Data
public class ProductKnowledgeDocDTO {
    private Long productId;
    private String productName;
    private String categoryName;
    private String brand;
    private String priceBand;
    private String sceneTags;
    private String featureTags;
    private String searchText;
    private String featureSummary;
}
