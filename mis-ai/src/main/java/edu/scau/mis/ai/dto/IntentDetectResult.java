package edu.scau.mis.ai.dto;

import edu.scau.mis.ai.enums.AiIntentType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class IntentDetectResult {
    private AiIntentType intentType;
    private String keyword;
    private Long productId;
    private Long couponId;
    private String orderSn;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String categoryName;
    private List<String> tags;
}
