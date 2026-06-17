package edu.scau.mis.product.mapper;

import edu.scau.mis.common.domain.ProductFeatureEmbedding;
import org.apache.ibatis.annotations.Param;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Primary
public interface IProductFeatureEmbeddingMapper {

    ProductFeatureEmbedding selectByProductId(Long productId);

    List<ProductFeatureEmbedding> selectAll();

    List<ProductFeatureEmbedding> selectByProductIds(@Param("productIds") List<Long> productIds);

    int insert(ProductFeatureEmbedding embedding);

    int update(ProductFeatureEmbedding embedding);
}
