package edu.scau.mis.ai.controller;

import edu.scau.mis.ai.dto.ProductRecommendRequest;
import edu.scau.mis.ai.service.ProductRecommendService;
import edu.scau.mis.ai.vo.ProductRecommendResponseVo;
import edu.scau.mis.common.domain.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai/product")
@RequiredArgsConstructor
public class ProductRecommendController {

    private final ProductRecommendService productRecommendService;

    @PostMapping("/recommend")
    public ApiResult<ProductRecommendResponseVo> recommend(@RequestBody ProductRecommendRequest request) {
        return ApiResult.success(productRecommendService.recommend(request));
    }
}