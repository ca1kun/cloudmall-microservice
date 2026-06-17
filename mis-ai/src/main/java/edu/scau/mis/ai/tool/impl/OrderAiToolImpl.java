package edu.scau.mis.ai.tool.impl;

import edu.scau.mis.ai.dto.OrderQueryDTO;
import edu.scau.mis.ai.feign.RemoteOrderFeign;
import edu.scau.mis.ai.feign.RemoteProductFeign;
import edu.scau.mis.ai.tool.OrderAiTool;
import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.common.domain.OmsOrder;
import edu.scau.mis.common.domain.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class OrderAiToolImpl implements OrderAiTool {

    @Autowired
    private RemoteOrderFeign remoteOrderFeign;

    @Autowired
    private RemoteProductFeign remoteProductFeign;

    @Override
    public OrderQueryDTO queryOrder(Long memberId, String orderSn) {
        if (memberId == null || orderSn == null || orderSn.isBlank()) return null;
        try {
            ApiResult<OmsOrder> result = remoteOrderFeign.getOrderDetail(memberId, orderSn);
            if (result == null || result.getData() == null) return null;
            OmsOrder order = result.getData();
            OrderQueryDTO dto = new OrderQueryDTO();
            dto.setOrderId(order.getId());
            dto.setOrderSn(order.getOrderSn());
            dto.setStatus(order.getStatus());
            dto.setTotalAmount(order.getTotalAmount());
            dto.setPayAmount(order.getPayAmount());
            dto.setProductNames(new ArrayList<>());
            return dto;
        } catch (Exception e) {
            log.warn("queryOrder failed, memberId={}, orderSn={}", memberId, orderSn, e);
            return null;
        }
    }

    @Override
    public Map<String, Object> createOrder(Long memberId, Long productId, Integer quantity) {
        if (memberId == null) return Map.of("error", "请先登录后再下单");
        if (productId == null) return Map.of("error", "未找到对应商品，请提供更具体的商品名称");
        try {
            ApiResult<Map<String, Object>> result = remoteOrderFeign.createOrder(memberId, productId, quantity);
            return result == null || result.getData() == null ? Map.of() : result.getData();
        } catch (Exception e) {
            log.warn("createOrder failed, memberId={}, productId={}", memberId, productId, e);
            return Map.of("error", "下单失败: " + e.getMessage());
        }
    }

    @Override
    public String cancelOrder(Long memberId, String orderSn) {
        if (memberId == null) return "请先登录后再操作";
        try {
            ApiResult<String> result = remoteOrderFeign.cancelOrder(memberId, orderSn);
            return result == null ? "取消失败" : result.getMessage();
        } catch (Exception e) {
            log.warn("cancelOrder failed, memberId={}, orderSn={}", memberId, orderSn, e);
            return "取消订单失败: " + e.getMessage();
        }
    }

    /**
     * 通过关键词查找商品并下单
     */
    public Map<String, Object> createOrderByKeyword(Long memberId, String keyword, Integer quantity) {
        if (memberId == null) return Map.of("error", "请先登录后再下单");
        if (keyword == null || keyword.isBlank()) return Map.of("error", "请告诉我您想购买的商品名称");

        try {
            ApiResult<List<Product>> searchResult = remoteProductFeign.searchProducts(keyword, null, null, null);
            if (searchResult == null || searchResult.getData() == null || searchResult.getData().isEmpty()) {
                return Map.of("error", "未找到商品「" + keyword + "」，请尝试更具体的名称");
            }
            Product product = searchResult.getData().get(0);
            return createOrder(memberId, product.getProductId(), quantity);
        } catch (Exception e) {
            log.warn("createOrderByKeyword failed, memberId={}, keyword={}", memberId, keyword, e);
            return Map.of("error", "下单失败: " + e.getMessage());
        }
    }
}
