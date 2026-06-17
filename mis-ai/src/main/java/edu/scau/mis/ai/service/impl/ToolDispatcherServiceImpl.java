package edu.scau.mis.ai.service.impl;

import edu.scau.mis.ai.dto.AiChatRequest;
import edu.scau.mis.ai.dto.IntentDetectResult;
import edu.scau.mis.ai.enums.AiIntentType;
import edu.scau.mis.ai.service.ToolDispatcherService;
import edu.scau.mis.ai.tool.CartAiTool;
import edu.scau.mis.ai.tool.CouponAiTool;
import edu.scau.mis.ai.tool.OrderAiTool;
import edu.scau.mis.ai.tool.ProductAiTool;
import edu.scau.mis.ai.tool.impl.CartAiToolImpl;
import edu.scau.mis.ai.tool.impl.OrderAiToolImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ToolDispatcherServiceImpl implements ToolDispatcherService {

    @Autowired
    private ProductAiTool productAiTool;
    @Autowired
    private OrderAiTool orderAiTool;
    @Autowired
    private CartAiTool cartAiTool;
    @Autowired
    private CouponAiTool couponAiTool;
    @Autowired
    private OrderAiToolImpl orderAiToolImpl;
    @Autowired
    private CartAiToolImpl cartAiToolImpl;

    @Override
    public Object dispatch(AiIntentType intentType, AiChatRequest request, IntentDetectResult intentResult) {
        try {
            return switch (intentType) {
                case PRODUCT_QA -> productAiTool.getProductDetail(intentResult.getKeyword());
                case PRODUCT_RECOMMEND -> productAiTool.recommendProducts(intentResult);
                case ORDER_QUERY -> orderAiTool.queryOrder(request.getMemberId(), intentResult.getOrderSn());
                case ORDER_CREATE -> dispatchCreateOrder(request, intentResult);
                case ORDER_CANCEL -> orderAiTool.cancelOrder(request.getMemberId(), intentResult.getOrderSn());
                case CART_QUERY -> cartAiTool.queryCartSummary(request.getMemberId());
                case CART_ADD -> dispatchAddToCart(request, intentResult);
                case CART_REMOVE -> dispatchRemoveFromCart(request, intentResult);
                case COUPON_QUERY -> couponAiTool.queryAvailableCoupons(request.getMemberId());
                case COUPON_CLAIM -> couponAiTool.claimCoupon(request.getMemberId(), intentResult.getCouponId());
                default -> null;
            };
        } catch (Exception e) {
            log.warn("Tool dispatch failed, intentType={}, error={}", intentType, e.getMessage());
            return null;
        }
    }

    /**
     * 下单：如果有 productId 直接下单，否则用 keyword 查商品后下单
     */
    private Object dispatchCreateOrder(AiChatRequest request, IntentDetectResult intentResult) {
        Long productId = intentResult.getProductId();
        if (productId != null) {
            return orderAiTool.createOrder(request.getMemberId(), productId, 1);
        }
        // productId 为 null，用 keyword 查商品
        return orderAiToolImpl.createOrderByKeyword(request.getMemberId(), intentResult.getKeyword(), 1);
    }

    /**
     * 加购：如果有 productId 直接加购，否则用 keyword 查商品后加购
     */
    private Object dispatchAddToCart(AiChatRequest request, IntentDetectResult intentResult) {
        Long productId = intentResult.getProductId();
        if (productId != null) {
            return cartAiTool.addToCart(request.getMemberId(), productId, 1);
        }
        // productId 为 null，用 keyword 查商品
        return cartAiToolImpl.addToCartByKeyword(request.getMemberId(), intentResult.getKeyword(), 1);
    }

    /**
     * 移除购物车：如果有 productId 直接移除，否则用 keyword 查商品后移除
     */
    private Object dispatchRemoveFromCart(AiChatRequest request, IntentDetectResult intentResult) {
        Long productId = intentResult.getProductId();
        if (productId != null) {
            return cartAiTool.removeFromCart(request.getMemberId(), productId);
        }
        return cartAiTool.removeFromCartByKeyword(request.getMemberId(), intentResult.getKeyword());
    }
}
