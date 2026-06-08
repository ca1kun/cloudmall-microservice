package edu.scau.mis.order.service;

import edu.scau.mis.common.dto.PageResult;
import edu.scau.mis.order.dto.OrderPageQueryDTO;
import edu.scau.mis.order.dto.ReturnApplyDTO;
import edu.scau.mis.order.dto.ReturnAuditDTO;
import edu.scau.mis.order.service.OrderApiService;
import edu.scau.mis.order.vo.OrderDetailResponseVO;
import edu.scau.mis.order.vo.OrderListVO;
import edu.scau.mis.order.vo.OrderStatusCountVO;

import java.util.List;

public interface OrderApiService {
    PageResult<OrderListVO> mallPage(OrderPageQueryDTO queryDTO);
    OrderDetailResponseVO mallDetail(Long orderId);
    List<OrderStatusCountVO> mallStatusCount();
    void applyReturn(Long orderId, ReturnApplyDTO dto);
    PageResult<OrderListVO> merchantPage(OrderPageQueryDTO queryDTO);
    OrderDetailResponseVO merchantDetail(Long orderId);
    List<OrderStatusCountVO> merchantStatusCount();
    void auditReturn(Long orderId, ReturnAuditDTO dto);
}
