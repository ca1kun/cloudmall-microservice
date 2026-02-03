package edu.scau.mis.pay.feign;

import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.common.domain.OmsOrder;
import edu.scau.mis.pay.config.FeignConfig; // 记得加上 Token 传递拦截器
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "mis-order", configuration = FeignConfig.class)
public interface RemoteOrderService {

    // 调用 mis-order 的查询接口
    // 假设你在 mis-order 的 OrderController 里有这个接口
    @GetMapping("/order/{id}")
    ApiResult<OmsOrder> getOrderById(@PathVariable("id") Long id);
}