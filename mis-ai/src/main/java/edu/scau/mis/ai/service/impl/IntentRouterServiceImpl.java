package edu.scau.mis.ai.service.impl;

import edu.scau.mis.ai.dto.IntentDetectResult;
import edu.scau.mis.ai.enums.AiIntentType;
import edu.scau.mis.ai.service.IntentRouterService;
import edu.scau.mis.ai.util.PriceExtractUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class IntentRouterServiceImpl implements IntentRouterService {

    private static final Pattern ORDER_SN_PATTERN = Pattern.compile("\\d{10,}");

    @Override
    public IntentDetectResult detect(String message) {
        String normalized = message == null ? "" : message.trim();
        IntentDetectResult result = new IntentDetectResult();
        result.setIntentType(AiIntentType.UNKNOWN);
        result.setKeyword(normalized);
        result.setTags(extractTags(normalized));

        if (containsAny(normalized, "取消订单", "删除订单")) {
            result.setIntentType(AiIntentType.ORDER_CANCEL);
            result.setOrderSn(extractOrderSn(normalized));
            return result;
        }
        if (containsAny(normalized, "下单", "购买", "帮我买", "我要买", "创建订单")) {
            result.setIntentType(AiIntentType.ORDER_CREATE);
            result.setKeyword(extractKeyword(normalized));
            return result;
        }
        if (containsAny(normalized, "订单", "物流", "发货", "我的订单")) {
            result.setIntentType(AiIntentType.ORDER_QUERY);
            result.setOrderSn(extractOrderSn(normalized));
            return result;
        }
        if (containsAny(normalized, "加入购物车", "加进购物车", "添加购物车", "加到购物车", "放进购物车")) {
            result.setIntentType(AiIntentType.CART_ADD);
            result.setKeyword(extractKeyword(normalized));
            return result;
        }
        if (containsAny(normalized, "删除购物车", "删去购物车", "移除购物车", "清空购物车", "删掉购物车", "去掉购物车")) {
            result.setIntentType(AiIntentType.CART_REMOVE);
            result.setKeyword(extractKeyword(normalized));
            return result;
        }
        if (containsAny(normalized, "领券", "领取优惠券", "领优惠券")) {
            result.setIntentType(AiIntentType.COUPON_CLAIM);
            return result;
        }
        if (containsAny(normalized, "购物车", "加购")) {
            result.setIntentType(AiIntentType.CART_QUERY);
            return result;
        }
        if (containsAny(normalized, "优惠券", "满减", "券")) {
            result.setIntentType(AiIntentType.COUPON_QUERY);
            return result;
        }
        if (containsAny(normalized, "推荐", "预算", "适合", "哪个好")) {
            result.setIntentType(AiIntentType.PRODUCT_RECOMMEND);
            fillPriceRange(result, normalized);
            fillCategory(result, normalized);
            return result;
        }
        if (containsAny(normalized, "退货", "发票", "售后", "规则")) {
            result.setIntentType(AiIntentType.POLICY_QA);
            return result;
        }
        if (containsAny(normalized, "手机", "电脑", "洗发水", "商品")) {
            result.setIntentType(AiIntentType.PRODUCT_QA);
            fillPriceRange(result, normalized);
            fillCategory(result, normalized);
            return result;
        }
        result.setIntentType(AiIntentType.SMALL_TALK);
        return result;
    }

    private String extractKeyword(String message) {
        if (message == null) return null;
        return message
                .replaceAll("(?i)(请|帮|把|给|我|要|想|吧|呢|啊|的|了|里|中|一下|一瓶|一件|一个|两瓶|两件)", "")
                .replaceAll("(?i)加入购物车|加进购物车|添加购物车|加到购物车|放进购物车|删除购物车|删去购物车|删掉购物车|去掉购物车|移除购物车|清空购物车|下单|购买|帮我买|我要买|创建订单|取消订单|删除订单", "")
                .trim();
    }

    private void fillPriceRange(IntentDetectResult result, String message) {
        BigDecimal budget = PriceExtractUtil.extractBudget(message);
        if (budget != null) {
            result.setMinPrice(budget.subtract(BigDecimal.valueOf(500)).max(BigDecimal.ZERO));
            result.setMaxPrice(budget.add(BigDecimal.valueOf(500)));
        }
    }

    private void fillCategory(IntentDetectResult result, String message) {
        if (message.contains("手机")) result.setCategoryName("手机");
        else if (message.contains("电脑")) result.setCategoryName("电脑");
        else if (message.contains("文具")) result.setCategoryName("文具");
        else if (message.contains("洗发水") || message.contains("洗发") || message.contains("洗护")
                || message.contains("海飞丝") || message.contains("飘柔") || message.contains("潘婷")
                || message.contains("沙宣") || message.contains("清扬") || message.contains("施华蔻"))
            result.setCategoryName("洗护");
    }

    private List<String> extractTags(String message) {
        List<String> tags = new ArrayList<>();
        if (message.contains("拍照")) tags.add("拍照");
        if (message.contains("游戏")) tags.add("游戏");
        if (message.contains("性价比")) tags.add("性价比");
        if (message.contains("学生")) tags.add("学生");
        if (message.contains("续航")) tags.add("续航");
        if (message.contains("快充")) tags.add("快充");
        return tags;
    }

    private String extractOrderSn(String message) {
        if (message == null) return null;
        Matcher m = ORDER_SN_PATTERN.matcher(message.replaceAll("[^0-9]", ""));
        if (m.find()) return m.group();
        String[] parts = message.replaceAll("[^0-9]", " ").trim().split("\\s+");
        for (String part : parts) {
            if (part.length() >= 8) return part;
        }
        return null;
    }

    private boolean containsAny(String message, String... keywords) {
        for (String k : keywords) if (message.contains(k)) return true;
        return false;
    }
}
