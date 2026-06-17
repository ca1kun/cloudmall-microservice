package edu.scau.mis.ai.tool;

import edu.scau.mis.ai.dto.OrderQueryDTO;

import java.util.Map;

public interface OrderAiTool {
    OrderQueryDTO queryOrder(Long memberId, String orderSn);
    Map<String, Object> createOrder(Long memberId, Long productId, Integer quantity);
    String cancelOrder(Long memberId, String orderSn);
}
