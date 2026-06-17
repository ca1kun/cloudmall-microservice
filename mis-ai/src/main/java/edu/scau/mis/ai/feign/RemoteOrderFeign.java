package edu.scau.mis.ai.feign;

import edu.scau.mis.ai.config.FeignConfig;
import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.common.domain.OmsOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "mis-order", configuration = FeignConfig.class)
public interface RemoteOrderFeign {

    @GetMapping("/order/ai/detail")
    ApiResult<OmsOrder> getOrderDetail(@RequestParam("memberId") Long memberId,
                                       @RequestParam("orderSn") String orderSn);

    @PostMapping("/order/ai/create")
    ApiResult<Map<String, Object>> createOrder(@RequestParam("memberId") Long memberId,
                                               @RequestParam("productId") Long productId,
                                               @RequestParam(value = "quantity", defaultValue = "1") Integer quantity);

    @PostMapping("/order/ai/cancel")
    ApiResult<String> cancelOrder(@RequestParam("memberId") Long memberId,
                                  @RequestParam("orderSn") String orderSn);
}
