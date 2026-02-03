package edu.scau.mis.cart.feign;

import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.common.domain.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 远程调用商品服务
 * name: 对方服务在 Nacos 里的名字
 * url: 对方服务的地址 (过渡阶段可以直接写死 IP:端口)
 */
@FeignClient(name = "mis-api")
public interface RemoteProductService {

    @GetMapping("/product/{id}")
    ApiResult<Product> getProductById(@PathVariable("id") Long id);

    /**
     * 批量查询商品
     */
    @PostMapping("/product/list/ids")
    ApiResult<List<Product>> getProductsByIds(@RequestBody List<Long> ids);
}