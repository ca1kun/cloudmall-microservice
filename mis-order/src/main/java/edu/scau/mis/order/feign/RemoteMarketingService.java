package edu.scau.mis.order.feign;

import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.common.domain.SmsCoupon;
import edu.scau.mis.order.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "mis-marketing", configuration = FeignConfig.class)
public interface RemoteMarketingService {

    @GetMapping("/coupon/info/{id}")
    ApiResult<SmsCoupon> getCouponInfo(@PathVariable("id") Long id);

    @PostMapping("/coupon/use")
    ApiResult<String> useCoupon(@RequestParam("couponId") Long couponId, @RequestParam("orderId") Long orderId);
}