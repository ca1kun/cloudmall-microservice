package edu.scau.mis.ai.feign;

import edu.scau.mis.ai.config.FeignConfig;
import edu.scau.mis.common.domain.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "mis-cart", configuration = FeignConfig.class)
public interface RemoteCartFeign {

    @GetMapping("/cart/ai/summary")
    ApiResult<Map<String, Object>> getCartSummary(@RequestParam("memberId") Long memberId);

    @PostMapping("/cart/ai/add")
    ApiResult<String> addToCart(@RequestParam("memberId") Long memberId,
                                @RequestParam("productId") Long productId,
                                @RequestParam(value = "quantity", defaultValue = "1") Integer quantity);

    @DeleteMapping("/cart/ai/remove")
    ApiResult<String> removeFromCart(@RequestParam("memberId") Long memberId,
                                     @RequestParam("productId") Long productId);
}
