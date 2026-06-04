package edu.scau.mis.ai.vo;

import lombok.Data;

import java.util.List;

@Data
public class ProductRecommendResponseVo {

    private String summary;

    private List<ProductRecommendItemVo> items;
}