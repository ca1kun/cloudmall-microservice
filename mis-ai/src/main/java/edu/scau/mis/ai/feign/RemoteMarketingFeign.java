package edu.scau.mis.ai.feign;

import edu.scau.mis.ai.config.FeignConfig;
import edu.scau.mis.common.domain.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(name = "mis-marketing", configuration = FeignConfig.class)
public interface RemoteMarketingFeign {

    @GetMapping("/coupon/ai/available")
    ApiResult<List<Map<String, Object>>> getAvailableCoupons(@RequestParam("memberId") Long memberId);

    @PostMapping("/coupon/ai/claim")
    ApiResult<String> claimCoupon(@RequestParam("memberId") Long memberId,
                                  @RequestParam("couponId") Long couponId);
}
