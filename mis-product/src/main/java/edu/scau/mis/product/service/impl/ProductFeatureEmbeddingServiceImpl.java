package edu.scau.mis.product.service.impl;

import edu.scau.mis.common.domain.ProductFeatureEmbedding;
import edu.scau.mis.product.mapper.IProductFeatureEmbeddingMapper;
import edu.scau.mis.product.service.IProductFeatureEmbeddingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProductFeatureEmbeddingServiceImpl implements IProductFeatureEmbeddingService {

    @Autowired
    private IProductFeatureEmbeddingMapper embeddingMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductFeatureEmbedding saveOrUpdate(ProductFeatureEmbedding embedding) {
        ProductFeatureEmbedding existing = embeddingMapper.selectByProductId(embedding.getProductId());
        if (existing == null) {
            embeddingMapper.insert(embedding);
        } else {
            embedding.setEmbeddingId(existing.getEmbeddingId());
            embeddingMapper.update(embedding);
        }
        return embeddingMapper.selectByProductId(embedding.getProductId());
    }

    @Override
    public List<ProductFeatureEmbedding> listAll() {
        return embeddingMapper.selectAll();
    }

    @Override
    public Map<Long, ProductFeatureEmbedding> getEmbeddingMap(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Map.of();
        }
        return embeddingMapper.selectByProductIds(productIds).stream()
                .collect(Collectors.toMap(ProductFeatureEmbedding::getProductId, Function.identity(), (a, b) -> a));
    }
}
