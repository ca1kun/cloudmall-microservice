package edu.scau.mis.api.controller;

import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.common.domain.ProductFeature;
import edu.scau.mis.product.service.IProductFeatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product/feature")
public class ProductFeatureController {

    @Autowired
    private IProductFeatureService productFeatureService;

    @PostMapping("/refresh")
    public ApiResult<List<ProductFeature>> refreshAll() {
        return ApiResult.success("商品特征刷新成功", productFeatureService.refreshAllFeatures());
    }

    @GetMapping("/docs")
    public ApiResult<List<ProductFeature>> docs(@RequestParam(value = "keyword", required = false) String keyword,
                                                @RequestParam(value = "categoryName", required = false) String categoryName,
                                                @RequestParam(value = "limit", required = false, defaultValue = "6") Integer limit) {
        List<String> tokens = extractTokens(keyword, categoryName);
        List<ProductFeature> features = productFeatureService.refreshAllFeatures().stream()
                .sorted(Comparator.comparingInt((ProductFeature feature) -> score(feature, tokens)).reversed())
                .filter(feature -> score(feature, tokens) > 0 || tokens.isEmpty())
                .limit(limit)
                .collect(Collectors.toList());
        return ApiResult.success(features);
    }

    private int score(ProductFeature feature, List<String> tokens) {
        String text = normalize(feature.getSearchText()) + " " + normalize(feature.getFeatureSummary());
        int score = 0;
        for (String token : tokens) {
            if (text.contains(token)) {
                score += 10;
            }
            if (normalize(feature.getFeatureTags()).contains(token) || normalize(feature.getSceneTags()).contains(token)) {
                score += 15;
            }
            if (normalize(feature.getCategoryName()).contains(token)) {
                score += 18;
            }
        }
        return score;
    }

    private List<String> extractTokens(String keyword, String categoryName) {
        return (normalize(keyword) + " " + normalize(categoryName))
                .replaceAll("[^\\p{IsAlphabetic}\\p{IsDigit}\\p{IsIdeographic}]+", " ")
                .lines()
                .flatMap(line -> List.of(line.split("\\s+")).stream())
                .map(String::trim)
                .filter(token -> !token.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
