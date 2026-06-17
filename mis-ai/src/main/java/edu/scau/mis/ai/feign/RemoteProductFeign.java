package edu.scau.mis.ai.feign;

import edu.scau.mis.ai.config.FeignConfig;
import edu.scau.mis.ai.dto.ProductKnowledgeDocDTO;
import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.common.domain.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "mis-api", configuration = FeignConfig.class)
public interface RemoteProductFeign {

    @GetMapping("/product/ai/search")
    ApiResult<List<Product>> searchProducts(@RequestParam(value = "keyword", required = false) String keyword,
                                            @RequestParam(value = "categoryName", required = false) String categoryName,
                                            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
                                            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice);

    @GetMapping("/product/feature/docs")
    ApiResult<List<ProductKnowledgeDocDTO>> getFeatureDocs(@RequestParam(value = "keyword", required = false) String keyword,
                                                           @RequestParam(value = "categoryName", required = false) String categoryName,
                                                           @RequestParam(value = "limit", required = false) Integer limit);
}
