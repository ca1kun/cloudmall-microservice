package edu.scau.mis.order.feign;

import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.common.dto.StockLockDTO;
import edu.scau.mis.order.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "mis-api", configuration = FeignConfig.class)
public interface RemoteProductService {
    // 扣减库存 (需要在 mis-web 实现这个接口)
    @PostMapping("/product/lockStock")
    ApiResult<String> lockStock(@RequestBody List<StockLockDTO> stockLockList);

    // 👇 新增：恢复库存
    @PostMapping("/product/unlockStock")
    ApiResult<String> unlockStock(@RequestBody List<StockLockDTO> stockLockList);
}