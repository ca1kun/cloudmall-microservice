package edu.scau.mis.order.controller;

import edu.scau.mis.common.domain.ApiResult;

import edu.scau.mis.common.dto.PageResult;
import edu.scau.mis.order.dto.OrderPageQueryDTO;
import edu.scau.mis.order.dto.ReturnApplyDTO;
import edu.scau.mis.order.service.OrderApiService;
import edu.scau.mis.order.vo.OrderDetailResponseVO;
import edu.scau.mis.order.vo.OrderListVO;
import edu.scau.mis.order.vo.OrderStatusCountVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mall/orders")
public class MallOrderController {

    @Autowired
    private OrderApiService orderApiService;

    @GetMapping("/page")
    public ApiResult<PageResult<OrderListVO>> page(OrderPageQueryDTO queryDTO) {
        return ApiResult.success(orderApiService.mallPage(queryDTO));
    }

    @GetMapping("/{orderId}")
    public ApiResult<OrderDetailResponseVO> detail(@PathVariable Long orderId) {
        return ApiResult.success(orderApiService.mallDetail(orderId));
    }

    @GetMapping("/status-count")
    public ApiResult<List<OrderStatusCountVO>> statusCount() {
        return ApiResult.success(orderApiService.mallStatusCount());
    }

    @PostMapping("/{orderId}/return")
    public ApiResult<Void> applyReturn(@PathVariable Long orderId, @RequestBody ReturnApplyDTO dto) {
        orderApiService.applyReturn(orderId, dto);
        return ApiResult.success(null);
    }
}
