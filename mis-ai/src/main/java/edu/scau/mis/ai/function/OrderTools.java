package edu.scau.mis.ai.function;

import edu.scau.mis.ai.feign.RemoteOrderFeign;
import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.common.domain.OmsOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

/**
 * 订单工具 - 供 LLM 通过 Tool Calling 自主调用
 */
@Slf4j
@Component
public class OrderTools {

    private final RemoteOrderFeign remoteOrderFeign;

    public OrderTools(RemoteOrderFeign remoteOrderFeign) {
        this.remoteOrderFeign = remoteOrderFeign;
    }

    @Tool(description = "查询订单详情。根据订单编号查询订单信息，包括状态、金额等。")
    public String queryOrder(
            @ToolParam(description = "用户ID") Long memberId,
            @ToolParam(description = "订单编号") String orderSn
    ) {
        log.info("queryOrder called: memberId={}, orderSn={}", memberId, orderSn);
        if (memberId == null) return "请先登录";
        if (orderSn == null || orderSn.isBlank()) return "请提供订单编号";
        try {
            ApiResult<OmsOrder> result = remoteOrderFeign.getOrderDetail(memberId, orderSn);
            if (result == null || result.getData() == null) {
                return "未找到该订单";
            }
            OmsOrder order = result.getData();
            String statusText = switch (order.getStatus()) {
                case 0 -> "待付款";
                case 1 -> "待发货";
                case 2 -> "已发货";
                case 3 -> "已完成";
                case 4 -> "已关闭";
                default -> "未知";
            };
            return "订单编号: " + order.getOrderSn()
                 + ", 状态: " + statusText
                 + ", 总金额: " + order.getTotalAmount()
                 + ", 应付金额: " + order.getPayAmount();
        } catch (Exception e) {
            log.warn("queryOrder failed", e);
            return "查询订单失败: " + e.getMessage();
        }
    }

    @Tool(description = "取消订单。取消未发货的订单。")
    public String cancelOrder(
            @ToolParam(description = "用户ID") Long memberId,
            @ToolParam(description = "订单编号") String orderSn
    ) {
        log.info("cancelOrder called: memberId={}, orderSn={}", memberId, orderSn);
        if (memberId == null) return "请先登录";
        if (orderSn == null || orderSn.isBlank()) return "请提供订单编号";
        try {
            ApiResult<String> result = remoteOrderFeign.cancelOrder(memberId, orderSn);
            return result == null ? "取消失败" : result.getMessage();
        } catch (Exception e) {
            log.warn("cancelOrder failed", e);
            return "取消订单失败: " + e.getMessage();
        }
    }
}
