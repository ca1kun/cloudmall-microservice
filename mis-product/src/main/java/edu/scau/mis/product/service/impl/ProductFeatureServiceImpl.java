package edu.scau.mis.product.service.impl;

import edu.scau.mis.common.domain.Product;
import edu.scau.mis.common.domain.ProductFeature;
import edu.scau.mis.product.mapper.IProductFeatureMapper;
import edu.scau.mis.product.mapper.IProductMapper;
import edu.scau.mis.product.service.IProductFeatureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductFeatureServiceImpl implements IProductFeatureService {

    @Autowired
    private IProductMapper productMapper;

    @Autowired
    private IProductFeatureMapper productFeatureMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductFeature generateFeature(Long productId) {
        Product product = productMapper.selectProductById(productId);
        if (product == null) {
            return null;
        }
        ProductFeature feature = buildFeature(product);
        ProductFeature existing = productFeatureMapper.selectByProductId(productId);
        if (existing == null) {
            productFeatureMapper.insertProductFeature(feature);
        } else {
            feature.setFeatureId(existing.getFeatureId());
            productFeatureMapper.updateProductFeature(feature);
        }
        return productFeatureMapper.selectByProductId(productId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ProductFeature> refreshAllFeatures() {
        List<Product> products = productMapper.selectAllProductList();
        List<ProductFeature> result = new ArrayList<>();
        for (Product product : products) {
            ProductFeature feature = buildFeature(product);
            ProductFeature existing = productFeatureMapper.selectByProductId(product.getProductId());
            if (existing == null) {
                productFeatureMapper.insertProductFeature(feature);
            } else {
                feature.setFeatureId(existing.getFeatureId());
                productFeatureMapper.updateProductFeature(feature);
            }
            result.add(productFeatureMapper.selectByProductId(product.getProductId()));
        }
        log.info("product feature refresh complete, count={}", result.size());
        return result;
    }

    @Override
    public Map<Long, ProductFeature> getFeatureMap(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Map.of();
        }
        List<ProductFeature> existing = productFeatureMapper.selectByProductIds(productIds);
        Map<Long, ProductFeature> featureMap = existing.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(ProductFeature::getProductId, feature -> feature, (a, b) -> a));

        List<Long> missingIds = productIds.stream()
                .filter(id -> !featureMap.containsKey(id))
                .toList();
        for (Long missingId : missingIds) {
            ProductFeature generated = generateFeature(missingId);
            if (generated != null) {
                featureMap.put(generated.getProductId(), generated);
            }
        }
        return featureMap;
    }

    private ProductFeature buildFeature(Product product) {
        ProductFeature feature = new ProductFeature();
        feature.setProductId(product.getProductId());
        feature.setProductName(product.getProductName());
        feature.setCategoryName(product.getCategory() == null ? null : product.getCategory().getCategoryName());
        feature.setBrand(extractBrand(product.getProductName()));
        feature.setPriceBand(resolvePriceBand(product.getPrice()));
        feature.setSceneTags(joinTags(extractSceneTags(product)));
        feature.setFeatureTags(joinTags(extractFeatureTags(product)));
        feature.setFeatureSummary(buildSummary(product));
        feature.setSearchText(buildSearchText(product, feature));
        return feature;
    }

    private String extractBrand(String productName) {
        if (productName == null || productName.isBlank()) {
            return "unknown";
        }
        List<String> brands = List.of(
                "apple", "iphone", "huawei", "xiaomi", "redmi", "oppo", "vivo", "iqoo", "realme",
                "rog", "nubia", "lenovo", "thinkbook", "macbook", "hp", "dell", "aoc", "logitech",
                "razer", "keychron", "anker", "baseus", "romoss", "head-shoulders", "pantene", "vinda",
                "c&s", "pilot", "deli"
        );
        String normalized = productName.toLowerCase(Locale.ROOT);
        for (String brand : brands) {
            if (normalized.contains(brand)) {
                return brand;
            }
        }
        String[] parts = normalized.trim().split("\\s+");
        return parts.length == 0 ? normalized : parts[0];
    }

    private String resolvePriceBand(BigDecimal price) {
        if (price == null) {
            return "unknown";
        }
        if (price.compareTo(BigDecimal.valueOf(100)) < 0) {
            return "under-100";
        }
        if (price.compareTo(BigDecimal.valueOf(500)) < 0) {
            return "100-500";
        }
        if (price.compareTo(BigDecimal.valueOf(2000)) < 0) {
            return "500-2000";
        }
        if (price.compareTo(BigDecimal.valueOf(4000)) < 0) {
            return "2000-4000";
        }
        if (price.compareTo(BigDecimal.valueOf(7000)) < 0) {
            return "4000-7000";
        }
        return "7000+";
    }

    private List<String> extractSceneTags(Product product) {
        String text = fullText(product);
        Set<String> tags = new LinkedHashSet<>();
        addIf(tags, text, "gaming", "电竞", "游戏", "高刷", "散热");
        addIf(tags, text, "camera", "长焦", "拍照", "影像", "防抖");
        addIf(tags, text, "battery", "电池", "续航", "大电池");
        addIf(tags, text, "office", "轻薄", "办公", "商务", "便携");
        addIf(tags, text, "study", "学生", "考研", "真题");
        addIf(tags, text, "fast-charge", "快充", "充电", "100w", "120w", "67w");
        if (tags.isEmpty()) {
            if (text.contains("手机")) {
                tags.add("daily-use");
            } else if (text.contains("电脑") || text.contains("笔记本")) {
                tags.add("office-study");
            } else {
                tags.add("general");
            }
        }
        return new ArrayList<>(tags);
    }

    private List<String> extractFeatureTags(Product product) {
        String text = fullText(product);
        Set<String> tags = new LinkedHashSet<>();
        addIf(tags, text, "phone", "手机", "5g", "卫星通话", "电竞手机", "拍照手机");
        addIf(tags, text, "computer", "电脑", "笔记本", "轻薄本", "游戏本", "平板");
        addIf(tags, text, "accessory", "手机壳", "数据线", "充电宝", "快充", "配件");
        addIf(tags, text, "personal-care", "洗发", "去屑", "修护");
        addIf(tags, text, "stationery", "签字笔", "中性笔", "活页本", "错题本");
        addIf(tags, text, "snack", "坚果", "饼干", "派");
        return new ArrayList<>(tags);
    }

    private void addIf(Set<String> tags, String text, String tag, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword.toLowerCase(Locale.ROOT))) {
                tags.add(tag);
                return;
            }
        }
    }

    private String buildSummary(Product product) {
        List<String> parts = new ArrayList<>();
        parts.add(extractBrand(product.getProductName()));
        if (product.getCategory() != null && product.getCategory().getCategoryName() != null
                && !product.getCategory().getCategoryName().isBlank()) {
            parts.add(product.getCategory().getCategoryName());
        }
        parts.add(resolvePriceBand(product.getPrice()));
        List<String> sceneTags = extractSceneTags(product);
        if (!sceneTags.isEmpty()) {
            parts.add(sceneTags.get(0));
        }
        if (product.getStock() != null && product.getStock() > 0) {
            parts.add("in-stock");
        }
        return String.join(" ", parts);
    }

    private String buildSearchText(Product product, ProductFeature feature) {
        return SearchTextBuilder.builder()
                .add(product.getProductName())
                .add(product.getProductDescription())
                .add(product.getProductSn())
                .add(feature.getCategoryName())
                .add(feature.getBrand())
                .add(feature.getPriceBand())
                .add(feature.getSceneTags())
                .add(feature.getFeatureTags())
                .add(feature.getFeatureSummary())
                .build();
    }

    private String joinTags(List<String> tags) {
        return tags.stream()
                .filter(Predicate.not(String::isBlank))
                .distinct()
                .collect(Collectors.joining(","));
    }

    private String fullText(Product product) {
        return ((product.getProductName() == null ? "" : product.getProductName()) + " "
                + (product.getProductDescription() == null ? "" : product.getProductDescription()) + " "
                + (product.getCategory() == null || product.getCategory().getCategoryName() == null
                ? "" : product.getCategory().getCategoryName())).toLowerCase(Locale.ROOT);
    }

    private static final class SearchTextBuilder {
        private final List<String> values = new ArrayList<>();

        static SearchTextBuilder builder() {
            return new SearchTextBuilder();
        }

        SearchTextBuilder add(String value) {
            if (value != null && !value.isBlank()) {
                values.add(value.trim());
            }
            return this;
        }

        String build() {
            return String.join(" ", values);
        }
    }
}
