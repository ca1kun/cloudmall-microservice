package edu.scau.mis.ai.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.scau.mis.ai.dto.IntentDetectResult;
import edu.scau.mis.ai.enums.AiIntentType;
import edu.scau.mis.ai.service.IntentRouterService;
import edu.scau.mis.ai.service.LlmService;
import edu.scau.mis.ai.util.PriceExtractUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Primary
@Service
public class LlmIntentRouterServiceImpl implements IntentRouterService {

    @Autowired
    private LlmService llmService;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String INTENT_PROMPT = """
        你是商城AI助手的意图识别模块。根据用户的输入，判断意图并提取参数，以JSON格式返回。
        
        意图类型（intentType）必须从以下选择：
        - PRODUCT_RECOMMEND: 用户想推荐/搜索/比较商品
        - PRODUCT_QA: 用户询问特定商品的详细信息
        - ORDER_QUERY: 用户查询订单状态/物流
        - ORDER_CREATE: 用户想要下单/购买
        - ORDER_CANCEL: 用户要取消/删除订单
        - CART_QUERY: 用户查看购物车
        - CART_ADD: 用户要把商品加入购物车
        - CART_REMOVE: 用户要从购物车删除商品
        - COUPON_QUERY: 用户查询优惠券
        - COUPON_CLAIM: 用户要领取优惠券
        - POLICY_QA: 用户询问退换货/发票/售后等政策
        - SMALL_TALK: 用户只是闲聊/打招呼
        
        JSON字段说明（能提取就填，提取不到填null）：
        {
            "intentType": "意图类型",
            "keyword": "用户查询的关键词/商品名",
            "productId": 商品ID(数字),
            "couponId": 优惠券ID(数字),
            "orderSn": "订单号",
            "minPrice": 最低预算(数字),
            "maxPrice": 最高预算(数字),
            "categoryName": "商品类目(如手机、电脑等)",
            "tags": ["标签1","标签2"]（用户提到的特征如拍照、游戏、续航、性价比等）
        }
        
        只返回JSON，不要其他任何文字。""";

    @Override
    public IntentDetectResult detect(String message) {
        try {
            String llmResponse = llmService.chat(INTENT_PROMPT, message);
            String json = extractJson(llmResponse);
            Map<String, Object> map = objectMapper.readValue(json, Map.class);
            return buildResult(map, message);
        } catch (Exception e) {
            return fallbackKeywordMatch(message);
        }
    }

    private IntentDetectResult buildResult(Map<String, Object> map, String message) {
        IntentDetectResult result = new IntentDetectResult();
        result.setKeyword(message);

        String intentStr = getString(map, "intentType");
        result.setIntentType(parseIntent(intentStr));

        result.setOrderSn(getString(map, "orderSn"));
        result.setCategoryName(getString(map, "categoryName"));
        result.setKeyword(getString(map, "keyword"));

        if (map.get("productId") instanceof Number pid) result.setProductId(pid.longValue());
        if (map.get("couponId") instanceof Number cid) result.setCouponId(cid.longValue());
        if (map.get("minPrice") instanceof Number mp) result.setMinPrice(BigDecimal.valueOf(mp.doubleValue()));
        if (map.get("maxPrice") instanceof Number mp2) result.setMaxPrice(BigDecimal.valueOf(mp2.doubleValue()));

        Object tagsObj = map.get("tags");
        if (tagsObj instanceof List<?> tags) {
            result.setTags(tags.stream().map(Object::toString).toList());
        }

        return result;
    }

    private AiIntentType parseIntent(String s) {
        if (s == null || s.isBlank()) return AiIntentType.SMALL_TALK;
        try { return AiIntentType.valueOf(s.toUpperCase()); } catch (IllegalArgumentException e) { return AiIntentType.SMALL_TALK; }
    }

    private String getString(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v == null ? null : v.toString();
    }

    private String extractJson(String llmOutput) {
        int start = llmOutput.indexOf('{');
        int end = llmOutput.lastIndexOf('}');
        return (start >= 0 && end > start) ? llmOutput.substring(start, end + 1) : "{}";
    }

    private IntentDetectResult fallbackKeywordMatch(String message) {
        IntentDetectResult result = new IntentDetectResult();
        result.setKeyword(message);
        result.setIntentType(AiIntentType.SMALL_TALK);
        String m = message == null ? "" : message;
        if (m.contains("推荐") || m.contains("哪个好") || m.contains("买什么")) result.setIntentType(AiIntentType.PRODUCT_RECOMMEND);
        else if (m.contains("订单")) result.setIntentType(AiIntentType.ORDER_QUERY);
        else if (m.contains("购物车")) result.setIntentType(AiIntentType.CART_QUERY);
        else if (m.contains("优惠券")) result.setIntentType(AiIntentType.COUPON_QUERY);
        else if (containsAny(m, "手机", "电脑", "洗发")) result.setIntentType(AiIntentType.PRODUCT_QA);
        BigDecimal budget = PriceExtractUtil.extractBudget(message);
        if (budget != null) { result.setMinPrice(budget.subtract(BigDecimal.valueOf(500)).max(BigDecimal.ZERO)); result.setMaxPrice(budget.add(BigDecimal.valueOf(500))); }
        return result;
    }

    private boolean containsAny(String m, String... ks) { for (String k : ks) if (m.contains(k)) return true; return false; }
}
