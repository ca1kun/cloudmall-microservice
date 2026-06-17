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
public class ProductFeature extends BaseEntity implements Serializable {
    private Long featureId;
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
