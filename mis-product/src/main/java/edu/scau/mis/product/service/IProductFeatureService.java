package edu.scau.mis.product.service;

import edu.scau.mis.common.domain.ProductFeature;

import java.util.List;
import java.util.Map;

public interface IProductFeatureService {

    ProductFeature generateFeature(Long productId);

    List<ProductFeature> refreshAllFeatures();

    Map<Long, ProductFeature> getFeatureMap(List<Long> productIds);
}
