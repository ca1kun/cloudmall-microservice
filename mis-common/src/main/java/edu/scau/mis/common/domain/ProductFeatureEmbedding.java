package edu.scau.mis.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFeatureEmbedding extends BaseEntity implements Serializable {
    private Long embeddingId;
    private Long productId;
    private Integer dimensions;
    private String embeddingText;
    private String embeddingVector;
    private String modelName;
}
