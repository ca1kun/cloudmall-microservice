package edu.scau.mis.ai.tool.impl;

import edu.scau.mis.ai.dto.IntentDetectResult;
import edu.scau.mis.ai.dto.ProductRecommendItemDTO;
import edu.scau.mis.ai.feign.RemoteProductFeign;
import edu.scau.mis.ai.tool.ProductAiTool;
import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.common.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Component
public class ProductAiToolImpl implements ProductAiTool {

    private static final int MAX_RECOMMEND_COUNT = 5;

    @Autowired
    private RemoteProductFeign remoteProductFeign;

    @Override
    public List<ProductRecommendItemDTO> recommendProducts(IntentDetectResult intentResult) {
        ApiResult<List<Product>> result = remoteProductFeign.searchProducts(
                intentResult.getKeyword(),
                intentResult.getCategoryName(),
                intentResult.getMinPrice(),
                intentResult.getMaxPrice()
        );

        List<ProductRecommendItemDTO> list = new ArrayList<>();
        if (result == null || result.getData() == null) {
            return list;
        }

        result.getData().stream()
                .sorted(Comparator.comparing((Product product) -> score(product, intentResult)).reversed()
                        .thenComparing(Product::getPrice, Comparator.nullsLast(Comparator.naturalOrder())))
                .limit(MAX_RECOMMEND_COUNT)
                .forEach(product -> list.add(toRecommendItem(product, intentResult)));
        return list;
    }

    @Override
    public Object getProductDetail(String keyword) {
        ApiResult<List<Product>> result = remoteProductFeign.searchProducts(keyword, null, null, null);
        return result == null || result.getData() == null ? new ArrayList<>() : result.getData();
    }

    private ProductRecommendItemDTO toRecommendItem(Product product, IntentDetectResult intentResult) {
        ProductRecommendItemDTO dto = new ProductRecommendItemDTO();
        dto.setProductId(product.getProductId());
        dto.setProductName(product.getProductName());
        dto.setPrice(product.getPrice());
        dto.setImageUrl(product.getImageUrl());
        dto.setStock(product.getStock());
        dto.setReason(buildReason(product, intentResult));
        return dto;
    }

    private int score(Product product, IntentDetectResult intentResult) {
        int score = 0;
        if (product == null) {
            return 0;
        }

        if (product.getStock() != null && product.getStock() > 0) {
            score += 20;
        }
        if (intentResult.getCategoryName() != null
                && product.getCategory() != null
                && product.getCategory().getCategoryName() != null
                && product.getCategory().getCategoryName().contains(intentResult.getCategoryName())) {
            score += 40;
        }
        if (intentResult.getKeyword() != null) {
            String keyword = intentResult.getKeyword().toLowerCase(Locale.ROOT);
            if (contains(product.getProductName(), keyword)) {
                score += 25;
            }
            if (contains(product.getProductDescription(), keyword)) {
                score += 15;
            }
        }
        if (intentResult.getTags() != null) {
            for (String tag : intentResult.getTags()) {
                if (contains(product.getProductName(), tag) || contains(product.getProductDescription(), tag)) {
                    score += 18;
                }
            }
        }
        if (product.getPrice() != null) {
            score += priceScore(product.getPrice(), intentResult.getMinPrice(), intentResult.getMaxPrice());
        }
        return score;
    }

    private int priceScore(BigDecimal price, BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice == null && maxPrice == null) {
            return 10;
        }
        if (minPrice != null && maxPrice != null && price.compareTo(minPrice) >= 0 && price.compareTo(maxPrice) <= 0) {
            return 60;
        }

        BigDecimal target = minPrice != null && maxPrice != null
                ? minPrice.add(maxPrice).divide(BigDecimal.valueOf(2), 0, RoundingMode.HALF_UP)
                : (minPrice != null ? minPrice : maxPrice);
        BigDecimal distance = price.subtract(target).abs();
        if (distance.compareTo(BigDecimal.valueOf(500)) <= 0) {
            return 35;
        }
        if (distance.compareTo(BigDecimal.valueOf(1500)) <= 0) {
            return 20;
        }
        return 5;
    }

    private String buildReason(Product product, IntentDetectResult intentResult) {
        List<String> reasons = new ArrayList<>();
        if (product.getPrice() != null) {
            if (intentResult.getMinPrice() != null && intentResult.getMaxPrice() != null
                    && product.getPrice().compareTo(intentResult.getMinPrice()) >= 0
                    && product.getPrice().compareTo(intentResult.getMaxPrice()) <= 0) {
                reasons.add("预算匹配");
            } else if (intentResult.getMaxPrice() != null && product.getPrice().compareTo(intentResult.getMaxPrice()) > 0) {
                reasons.add("价格略高，但接近当前预算区间");
            } else if (intentResult.getMinPrice() != null && product.getPrice().compareTo(intentResult.getMinPrice()) < 0) {
                reasons.add("低于预算，性价比更高");
            }
        }
        if (intentResult.getTags() != null) {
            for (String tag : intentResult.getTags()) {
                if (contains(product.getProductName(), tag) || contains(product.getProductDescription(), tag)) {
                    reasons.add("偏好匹配：" + tag);
                }
            }
        }
        if (product.getStock() != null && product.getStock() > 0) {
            reasons.add("当前有库存");
        }
        if (reasons.isEmpty()) {
            reasons.add("与当前需求相关");
        }
        return String.join("；", reasons);
    }

    private boolean contains(String text, String keyword) {
        return text != null && keyword != null && text.toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT));
    }
}
