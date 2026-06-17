package edu.scau.mis.ai.tool;

import java.util.Map;

public interface CartAiTool {
    Map<String, Object> queryCartSummary(Long memberId);
    String addToCart(Long memberId, Long productId, Integer quantity);
    String removeFromCart(Long memberId, Long productId);
    String removeFromCartByKeyword(Long memberId, String keyword);
}
