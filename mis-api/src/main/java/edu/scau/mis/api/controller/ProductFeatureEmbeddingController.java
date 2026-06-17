package edu.scau.mis.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.common.domain.ProductFeatureEmbedding;
import edu.scau.mis.common.dto.ProductEmbeddingDocDTO;
import edu.scau.mis.common.dto.ProductEmbeddingRefreshItemDTO;
import edu.scau.mis.product.service.IProductFeatureEmbeddingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/product/feature/embedding")
public class ProductFeatureEmbeddingController {

    @Autowired
    private IProductFeatureEmbeddingService embeddingService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/docs")
    public ApiResult<List<ProductEmbeddingDocDTO>> docs(@RequestParam(value = "categoryName", required = false) String categoryName,
                                                        @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit) {
        List<ProductEmbeddingDocDTO> docs = new ArrayList<>();
        for (ProductFeatureEmbedding embedding : embeddingService.listAll()) {
            if (docs.size() >= limit) {
                break;
            }
            ProductEmbeddingDocDTO dto = new ProductEmbeddingDocDTO();
            dto.setProductId(embedding.getProductId());
            dto.setEmbeddingText(embedding.getEmbeddingText());
            dto.setEmbeddingVector(parseVector(embedding.getEmbeddingVector()));
            dto.setDimensions(embedding.getDimensions());
            dto.setModelName(embedding.getModelName());
            docs.add(dto);
        }
        return ApiResult.success(docs);
    }

    @PostMapping("/refresh")
    public ApiResult<String> refresh(@RequestBody List<ProductEmbeddingRefreshItemDTO> items) {
        for (ProductEmbeddingRefreshItemDTO item : items) {
            ProductFeatureEmbedding embedding = new ProductFeatureEmbedding();
            embedding.setProductId(item.getProductId());
            embedding.setDimensions(item.getDimensions());
            embedding.setEmbeddingText(item.getEmbeddingText());
            embedding.setEmbeddingVector(writeVector(item.getEmbeddingVector()));
            embedding.setModelName(item.getModelName());
            embeddingService.saveOrUpdate(embedding);
        }
        return ApiResult.success("商品向量刷新成功", null);
    }

    private String writeVector(List<Double> vector) {
        try {
            return objectMapper.writeValueAsString(vector);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    private List<Double> parseVector(String vector) {
        if (vector == null || vector.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(vector, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
}
