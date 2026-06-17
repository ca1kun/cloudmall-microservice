package edu.scau.mis.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductEmbeddingRefreshItemDTO {
    private Long productId;
    private Integer dimensions;
    private String embeddingText;
    private List<Double> embeddingVector;
    private String modelName;
}
