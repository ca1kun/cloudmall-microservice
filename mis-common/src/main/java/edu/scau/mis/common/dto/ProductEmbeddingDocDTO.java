package edu.scau.mis.common.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProductEmbeddingDocDTO {
    private Long productId;
    private String embeddingText;
    private List<Double> embeddingVector;
    private Integer dimensions;
    private String modelName;
}
