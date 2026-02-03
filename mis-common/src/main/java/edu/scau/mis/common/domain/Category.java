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
public class Category extends BaseEntity implements Serializable {
    private Long categoryId;
    private Long parentId;
    private String categoryName;
}
