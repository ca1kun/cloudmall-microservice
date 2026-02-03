package edu.scau.mis.order.feign;

import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.common.domain.OmsCartItem;
import edu.scau.mis.order.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "mis-cart", configuration = FeignConfig.class)
public interface RemoteCartService {
    // 获取当前用户的购物车列表
    @GetMapping("/cart/list")
    ApiResult<List<OmsCartItem>> list();
    // 新增批量删除方法
    @DeleteMapping("/cart/delete/batch")
    ApiResult<String> deleteBatch(@RequestBody List<Long> productIds);
}