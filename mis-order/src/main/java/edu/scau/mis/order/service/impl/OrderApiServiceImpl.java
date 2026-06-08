package edu.scau.mis.order.service.impl;

import edu.scau.mis.common.dto.PageResult;
import edu.scau.mis.order.dto.OrderPageQueryDTO;
import edu.scau.mis.order.dto.ReturnApplyDTO;
import edu.scau.mis.order.dto.ReturnAuditDTO;
import edu.scau.mis.order.enums.OrderStatusEnum;
import edu.scau.mis.order.mapper.OrderApiMapper;
import edu.scau.mis.order.service.OrderApiService;
import edu.scau.mis.order.util.CurrentMemberUtil;
import edu.scau.mis.order.vo.OrderDetailResponseVO;
import edu.scau.mis.order.vo.OrderListVO;
import edu.scau.mis.order.vo.OrderStatusCountVO;

import edu.scau.mis.order.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class OrderApiServiceImpl implements OrderApiService {

    @Autowired
    private OrderApiMapper orderApiMapper;

    @Override
    public PageResult<OrderListVO> mallPage(OrderPageQueryDTO queryDTO) {
        normalizePage(queryDTO);
        Long memberId = CurrentMemberUtil.getCurrentMemberId();
        Integer statusCode = OrderStatusEnum.codeOf(queryDTO.getStatus());
        Integer offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
        Long total = orderApiMapper.countMallPage(memberId, queryDTO, statusCode);
        List<OrderListVO> records = orderApiMapper.selectMallPage(memberId, queryDTO, statusCode, offset, queryDTO.getPageSize());
        fillStatus(records);
        return PageResult.of(records, total, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public OrderDetailResponseVO mallDetail(Long orderId) {
        Long memberId = CurrentMemberUtil.getCurrentMemberId();
        OrderDetailVO order = orderApiMapper.selectMallDetail(memberId, orderId);
        if (order == null) throw new IllegalArgumentException("订单不存在或无权查看");
        fillStatus(order);
        OrderDetailResponseVO result = new OrderDetailResponseVO();
        result.setOrder(order);
        result.setItems(orderApiMapper.selectItemsByOrderId(orderId));
        return result;
    }

    @Override
    public List<OrderStatusCountVO> mallStatusCount() {
        return buildStatusCount(orderApiMapper.selectMallStatusCount(CurrentMemberUtil.getCurrentMemberId()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void applyReturn(Long orderId, ReturnApplyDTO dto) {
        Long memberId = CurrentMemberUtil.getCurrentMemberId();
        String reason = dto == null ? null : dto.getReason();
        if (reason == null || reason.trim().length() < 10 || reason.trim().length() > 200) {
            throw new IllegalArgumentException("退货原因长度必须为10-200字");
        }
        Integer currentStatus = orderApiMapper.selectMallOrderStatus(memberId, orderId);
        if (currentStatus == null) throw new IllegalArgumentException("订单不存在或无权操作");
        if (!Objects.equals(currentStatus, OrderStatusEnum.PAID.getCode())
                && !Objects.equals(currentStatus, OrderStatusEnum.SHIPPED.getCode())
                && !Objects.equals(currentStatus, OrderStatusEnum.COMPLETED.getCode())) {
            throw new IllegalArgumentException("当前订单状态不允许申请退货");
        }
        int rows = orderApiMapper.applyReturn(memberId, orderId, OrderStatusEnum.REFUNDING.getCode(), reason.trim());
        if (rows <= 0) throw new IllegalArgumentException("申请退货失败");
    }

    @Override
    public PageResult<OrderListVO> merchantPage(OrderPageQueryDTO queryDTO) {
        normalizePage(queryDTO);
        Integer statusCode = OrderStatusEnum.codeOf(queryDTO.getStatus());
        Integer offset = (queryDTO.getPageNum() - 1) * queryDTO.getPageSize();
        Long total = orderApiMapper.countMerchantPage(queryDTO, statusCode);
        List<OrderListVO> records = orderApiMapper.selectMerchantPage(queryDTO, statusCode, offset, queryDTO.getPageSize());
        fillStatus(records);
        return PageResult.of(records, total, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public OrderDetailResponseVO merchantDetail(Long orderId) {
        OrderDetailVO order = orderApiMapper.selectMerchantDetail(orderId);
        if (order == null) throw new IllegalArgumentException("订单不存在");
        fillStatus(order);
        OrderDetailResponseVO result = new OrderDetailResponseVO();
        result.setOrder(order);
        result.setItems(orderApiMapper.selectItemsByOrderId(orderId));
        return result;
    }

    @Override
    public List<OrderStatusCountVO> merchantStatusCount() {
        return buildStatusCount(orderApiMapper.selectMerchantStatusCount());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditReturn(Long orderId, ReturnAuditDTO dto) {
        if (dto == null || dto.getApproved() == null) throw new IllegalArgumentException("approved不能为空");
        String remark = dto.getRemark();
        if (remark != null && remark.length() > 200) throw new IllegalArgumentException("审核备注最多200字");
        Integer currentStatus = orderApiMapper.selectMerchantOrderStatus(orderId);
        if (currentStatus == null) throw new IllegalArgumentException("订单不存在");
        if (!Objects.equals(currentStatus, OrderStatusEnum.REFUNDING.getCode())) {
            throw new IllegalArgumentException("仅退款中的订单可以审核退货");
        }
        Integer newStatus = dto.getApproved() ? OrderStatusEnum.REFUNDED.getCode() : OrderStatusEnum.RETURN_REJECTED.getCode();
        int rows = orderApiMapper.auditReturn(orderId, newStatus, remark);
        if (rows <= 0) throw new IllegalArgumentException("审核退货失败");
    }

    private void normalizePage(OrderPageQueryDTO queryDTO) {
        if (queryDTO == null) throw new IllegalArgumentException("查询参数不能为空");
        if (queryDTO.getPageNum() == null || queryDTO.getPageNum() <= 0) queryDTO.setPageNum(1);
        if (queryDTO.getPageSize() == null || queryDTO.getPageSize() <= 0) queryDTO.setPageSize(10);
        if (queryDTO.getPageSize() > 100) queryDTO.setPageSize(100);
    }

    private void fillStatus(List<OrderListVO> records) {
        if (records == null) return;
        for (OrderListVO record : records) record.setStatus(OrderStatusEnum.nameOf(record.getStatusCode()));
    }

    private void fillStatus(OrderDetailVO order) {
        if (order != null) order.setStatus(OrderStatusEnum.nameOf(order.getStatusCode()));
    }

    private List<OrderStatusCountVO> buildStatusCount(List<OrderStatusCountRow> rows) {
        Map<String, Long> countMap = new LinkedHashMap<>();
        long all = 0L;
        for (OrderStatusEnum status : OrderStatusEnum.values()) countMap.put(status.name(), 0L);
        if (rows != null) {
            for (OrderStatusCountRow row : rows) {
                String name = OrderStatusEnum.nameOf(row.getStatusCode());
                if (name == null) continue;
                long count = row.getCount() == null ? 0L : row.getCount();
                countMap.put(name, count);
                all += count;
            }
        }
        List<OrderStatusCountVO> result = new ArrayList<>();
        result.add(new OrderStatusCountVO("ALL", all));
        for (Map.Entry<String, Long> entry : countMap.entrySet()) result.add(new OrderStatusCountVO(entry.getKey(), entry.getValue()));
        return result;
    }
}
