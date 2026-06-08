package edu.scau.mis.order.controller;

import edu.scau.mis.common.domain.ApiResult;

import edu.scau.mis.order.dto.ReturnAuditDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import edu.scau.mis.common.dto.PageResult;
import edu.scau.mis.order.dto.OrderPageQueryDTO;
import edu.scau.mis.order.dto.ReturnApplyDTO;
import edu.scau.mis.order.service.OrderApiService;
import edu.scau.mis.order.vo.OrderDetailResponseVO;
import edu.scau.mis.order.vo.OrderListVO;
import edu.scau.mis.order.vo.OrderStatusCountVO;
import java.util.List;

@RestController
@RequestMapping("/merchant/orders")
public class MerchantOrderController {

    @Autowired
    private OrderApiService orderApiService;

    @GetMapping("/page")
    public ApiResult<PageResult<OrderListVO>> page(OrderPageQueryDTO queryDTO) {
        return ApiResult.success(orderApiService.merchantPage(queryDTO));
    }

    @GetMapping("/{orderId}")
    public ApiResult<OrderDetailResponseVO> detail(@PathVariable Long orderId) {
        return ApiResult.success(orderApiService.merchantDetail(orderId));
    }

    @GetMapping("/status-count")
    public ApiResult<List<OrderStatusCountVO>> statusCount() {
        return ApiResult.success(orderApiService.merchantStatusCount());
    }

    @PostMapping("/{orderId}/return/audit")
    public ApiResult<Void> auditReturn(@PathVariable Long orderId, @RequestBody ReturnAuditDTO dto) {
        orderApiService.auditReturn(orderId, dto);
        return ApiResult.success(null);
    }
}
