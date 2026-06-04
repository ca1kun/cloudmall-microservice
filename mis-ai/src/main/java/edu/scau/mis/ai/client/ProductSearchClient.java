package edu.scau.mis.ai.client;

import edu.scau.mis.ai.dto.ProductSearchCondition;
import edu.scau.mis.ai.vo.ProductCandidateVo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductSearchClient {

    private final RestClient.Builder restClientBuilder;

    /**
     * 商品搜索接口地址。
     * 你可以先写网关地址，例如：
     * http://localhost:8080/product/search
     * 或者：
     * http://localhost:8080/api/product/search
     */
    @Value("${mis.ai.product-search-url:http://localhost:8080/product/search}")
    private String productSearchUrl;

    public List<ProductCandidateVo> searchProducts(ProductSearchCondition condition) {
        try {
            Map<String, Object> response = restClientBuilder.build()
                    .post()
                    .uri(productSearchUrl)
                    .body(condition)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (response == null || response.get("data") == null) {
                return Collections.emptyList();
            }

            Object data = response.get("data");

            // 这里先简单处理。
            // 如果你的商品接口返回的是分页结构，例如 data.records，
            // 那就需要在这里取 records。
            return convertProductList(data);
        } catch (Exception e) {
            throw new RuntimeException("调用商品模块查询商品失败：" + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<ProductCandidateVo> convertProductList(Object data) {
        // 第一版这里建议你后面根据商品接口返回结构调整。
        // 如果接口直接返回 List<ProductCandidateVo>，可以用 ObjectMapper 转。
        return Collections.emptyList();
    }
}