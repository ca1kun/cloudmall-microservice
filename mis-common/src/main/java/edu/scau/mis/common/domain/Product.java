package edu.scau.mis.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity implements Serializable {
    private Long productId;
    private String productSn;
    private String productName;
    private String productDescription;
    private BigDecimal price;
    private Long productCategoryId;
    private Category category;
    private String imageUrl;
    private String detailUrl;
    private Integer stock; // 新增库存字段
}
