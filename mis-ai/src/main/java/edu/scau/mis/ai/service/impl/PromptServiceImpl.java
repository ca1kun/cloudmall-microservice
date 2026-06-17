package edu.scau.mis.ai.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.scau.mis.ai.dto.AiChatRequest;
import edu.scau.mis.ai.dto.ConversationMessageDTO;
import edu.scau.mis.ai.dto.ProductRecommendItemDTO;
import edu.scau.mis.ai.dto.RagDocumentDTO;
import edu.scau.mis.ai.enums.AiIntentType;
import edu.scau.mis.ai.service.PromptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PromptServiceImpl implements PromptService {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String buildSystemPrompt(AiIntentType intentType) {
        return switch (intentType) {
            case PRODUCT_RECOMMEND ->
                "你是商城AI导购助手。基于提供的候选商品数据回答，不允许推荐候选之外的型号。说明预算匹配度、库存、卖点，语气简洁友好。若候选为空，诚实告知并建议用户调整预算或需求。";
            case PRODUCT_QA ->
                "你是商城AI商品助手。基于提供的商品信息回答，不编造参数或价格。信息不足时直接说明。";
            case ORDER_QUERY ->
                "你是商城AI订单助手。基于提供的订单数据回答，不编造物流状态、支付结果或售后结论。";
            case ORDER_CREATE ->
                "你是商城AI下单助手。如果下单成功，告知用户订单号和后续流程；如果失败，说明原因并建议操作。";
            case ORDER_CANCEL ->
                "你是商城AI订单助手。告知用户取消结果。若成功提醒退款退回方式；若失败说明原因。";
            case CART_QUERY ->
                "你是商城AI购物助手。基于购物车数据告知用户商品数量、总价，并给出下一步操作建议（下单、使用优惠券等）。";
            case CART_ADD ->
                "你是商城AI购物助手。告知用户加购结果，并建议下一步操作（继续浏览、查看购物车、下单）。";
            case CART_REMOVE ->
                "你是商城AI购物助手。告知用户移除结果，并询问是否还需要其他商品。";
            case COUPON_QUERY ->
                "你是商城AI优惠助手。基于优惠券数据说明可用券、使用门槛和到期时间，建议最优使用方案。";
            case COUPON_CLAIM ->
                "你是商城AI优惠助手。告知领券结果，并基于用户需求建议如何使用该券。";
            case POLICY_QA ->
                "你是商城AI客服助手。基于给定规则回答退换货、发票、售后等政策，不虚构平台规则。";
            case SMALL_TALK ->
                "你是商城AI小助手。语气友好、简洁，可以引导用户尝试商品推荐、查订单、用优惠券等功能。";
            default ->
                "你是商城AI助手。优先依据给定数据回答，语言自然简洁，不编造事实。可以引导用户使用商品推荐、查订单等功能。";
        };
    }

    @Override
    public String buildUserPrompt(AiChatRequest request, Object toolResult, List<RagDocumentDTO> ragDocs) {
        StringBuilder b = new StringBuilder();
        b.append("用户问题: ").append(request.getMessage()).append('\n');
        b.append("用户ID: ").append(request.getMemberId()).append('\n');
        b.append("工具查询结果: ").append(formatToolResult(toolResult)).append('\n');
        b.append("RAG参考知识: ").append(writeValue(ragDocs)).append('\n');
        b.append("要求: 优先基于工具结果和RAG知识回答；若两者都为空，诚实说明并给出建议；不编造数据。");
        return b.toString();
    }

    public String buildUserPrompt(AiChatRequest request, Object toolResult, List<RagDocumentDTO> ragDocs,
                                  List<ConversationMessageDTO> history) {
        StringBuilder b = new StringBuilder();
        b.append("对话历史: ").append(writeValue(history.stream().map(m ->
            m.getRole() + ": " + m.getContent()).toList())).append('\n');
        b.append(buildUserPrompt(request, toolResult, ragDocs));
        return b.toString();
    }

    private String writeValue(Object value) {
        try { return objectMapper.writeValueAsString(value); }
        catch (JsonProcessingException e) { return String.valueOf(value); }
    }

    private String formatToolResult(Object result) {
        if (result == null) return "null";
        if (result instanceof String s) return s;
        if (!(result instanceof List<?> items)) return writeValue(result);
        if (items.isEmpty()) return "[]";
        List<String> lines = new ArrayList<>();
        for (Object item : items) {
            if (item instanceof ProductRecommendItemDTO p) {
                lines.add(String.format("productId=%s name=%s price=%s stock=%s reason=%s",
                    p.getProductId(), safe(p.getProductName()), p.getPrice(), p.getStock(), safe(p.getReason())));
            } else {
                lines.add(writeValue(item));
            }
        }
        return String.join(" | ", lines);
    }

    private String safe(String v) { return v == null ? "" : v; }
}
