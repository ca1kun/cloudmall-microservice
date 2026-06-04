package edu.scau.mis.ai.service;

import edu.scau.mis.ai.dto.ProductRecommendRequest;
import edu.scau.mis.ai.vo.ProductRecommendResponseVo;

public interface ProductRecommendService {

    ProductRecommendResponseVo recommend(ProductRecommendRequest request);
}