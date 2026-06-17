package edu.scau.mis.product.service;

import edu.scau.mis.common.domain.ProductFeatureEmbedding;

import java.util.List;
import java.util.Map;

public interface IProductFeatureEmbeddingService {

    ProductFeatureEmbedding saveOrUpdate(ProductFeatureEmbedding embedding);

    List<ProductFeatureEmbedding> listAll();

    Map<Long, ProductFeatureEmbedding> getEmbeddingMap(List<Long> productIds);
}
