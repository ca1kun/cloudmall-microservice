package edu.scau.mis.ai.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductSearchCondition {

    /**
     * 商品关键词，例如：耳机、手机、键盘
     */
    private String keyword;

    /**
     * 分类名称，例如：数码、耳机、服饰
     */
    private String categoryName;

    /**
     * 最低价格
     */
    private BigDecimal minPrice;

    /**
     * 最高价格
     */
    private BigDecimal maxPrice;

    /**
     * 使用场景，例如：学生、办公、运动、游戏
     */
    private String scene;

    /**
     * 用户关注的特性，例如：续航、降噪、轻便、性价比
     */
    private List<String> features;

    /**
     * 查询数量
     */
    private Integer limit = 10;
}