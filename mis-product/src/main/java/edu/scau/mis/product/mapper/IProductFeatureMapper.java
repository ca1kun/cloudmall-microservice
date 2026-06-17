package edu.scau.mis.product.mapper;

import edu.scau.mis.common.domain.ProductFeature;
import org.apache.ibatis.annotations.Param;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Primary
public interface IProductFeatureMapper {

    ProductFeature selectByProductId(Long productId);

    List<ProductFeature> selectAllFeatures();

    List<ProductFeature> selectByProductIds(@Param("productIds") List<Long> productIds);

    int insertProductFeature(ProductFeature productFeature);

    int updateProductFeature(ProductFeature productFeature);

    int deleteByProductId(Long productId);
}
