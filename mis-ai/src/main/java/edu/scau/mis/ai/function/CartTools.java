package edu.scau.mis.ai.function;

import edu.scau.mis.ai.feign.RemoteCartFeign;
import edu.scau.mis.common.domain.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 购物车工具 - 供 LLM 通过 Tool Calling 自主调用
 */
@Slf4j
@Component
public class CartTools {

    private final RemoteCartFeign remoteCartFeign;

    public CartTools(RemoteCartFeign remoteCartFeign) {
        this.remoteCartFeign = remoteCartFeign;
    }

    @Tool(description = "查询用户购物车。返回购物车中所有商品、数量和总价。")
    public String queryCart(
            @ToolParam(description = "用户ID") Long memberId
    ) {
        log.info("queryCart called: memberId={}", memberId);
        if (memberId == null) return "请先登录";
        try {
            ApiResult<Map<String, Object>> result = remoteCartFeign.getCartSummary(memberId);
            if (result == null || result.getData() == null) {
                return "购物车为空";
            }
            Map<String, Object> data = result.getData();
            StringBuilder sb = new StringBuilder();
            sb.append("购物车商品数: ").append(data.get("itemCount"))
              .append(", 总数量: ").append(data.get("totalQuantity"))
              .append(", 总金额: ").append(data.get("totalAmount")).append("\n");
            Object itemsObj = data.get("items");
            if (itemsObj instanceof List<?> items) {
                for (Object item : items) {
                    if (item instanceof Map<?, ?> m) {
                        sb.append("- 商品ID: ").append(m.get("productId"))
                          .append(", 名称: ").append(m.get("productName"))
                          .append(", 数量: ").append(m.get("quantity"))
                          .append(", 价格: ").append(m.get("price"))
                          .append("\n");
                    }
                }
            }
            return sb.toString();
        } catch (Exception e) {
            log.warn("queryCart failed", e);
            return "查询购物车失败: " + e.getMessage();
        }
    }

    @Tool(description = "添加商品到购物车。如果用户只说了商品名没给productId，请先调用searchProducts获取productId。")
    public String addToCart(
            @ToolParam(description = "用户ID") Long memberId,
            @ToolParam(description = "商品ID") Long productId,
            @ToolParam(description = "数量，默认1") Integer quantity
    ) {
        log.info("addToCart called: memberId={}, productId={}, quantity={}", memberId, productId, quantity);
        if (memberId == null) return "请先登录";
        if (productId == null) return "缺少商品ID，请先搜索商品获取productId";
        int qty = quantity != null ? quantity : 1;
        try {
            ApiResult<String> result = remoteCartFeign.addToCart(memberId, productId, qty);
            return result == null ? "加购失败" : result.getMessage();
        } catch (Exception e) {
            log.warn("addToCart failed", e);
            return "加购失败: " + e.getMessage();
        }
    }

    @Tool(description = "从购物车移除商品。可以传入productId直接移除，也可以传入keyword在购物车商品名中匹配后移除。")
    public String removeFromCart(
            @ToolParam(description = "用户ID") Long memberId,
            @ToolParam(description = "商品ID，如果知道可以直接传") Long productId,
            @ToolParam(description = "商品名称关键词，用于在购物车中匹配商品，如'海飞丝'") String keyword
    ) {
        log.info("removeFromCart called: memberId={}, productId={}, keyword={}", memberId, productId, keyword);
        if (memberId == null) return "请先登录";

        // 有 productId 直接移除
        if (productId != null) {
            try {
                ApiResult<String> result = remoteCartFeign.removeFromCart(memberId, productId);
                return result == null ? "移除失败" : result.getMessage();
            } catch (Exception e) {
                log.warn("removeFromCart by productId failed", e);
                return "移除失败: " + e.getMessage();
            }
        }

        // 有关键词，在购物车中匹配
        if (keyword != null && !keyword.isBlank()) {
            try {
                ApiResult<Map<String, Object>> cartResult = remoteCartFeign.getCartSummary(memberId);
                if (cartResult != null && cartResult.getData() != null && cartResult.getData().containsKey("items")) {
                    Object itemsObj = cartResult.getData().get("items");
                    if (itemsObj instanceof List<?> items) {
                        for (Object item : items) {
                            if (item instanceof Map<?, ?> m) {
                                String productName = m.get("productName") != null ? m.get("productName").toString() : "";
                                if (productName.contains(keyword)) {
                                    Object pidObj = m.get("productId");
                                    if (pidObj != null) {
                                        Long pid = Long.valueOf(pidObj.toString());
                                        ApiResult<String> removeResult = remoteCartFeign.removeFromCart(memberId, pid);
                                        return removeResult == null ? "移除失败" : "已从购物车移除「" + productName + "」";
                                    }
                                }
                            }
                        }
                    }
                }
                return "购物车中未找到包含「" + keyword + "」的商品";
            } catch (Exception e) {
                log.warn("removeFromCart by keyword failed", e);
                return "移除失败: " + e.getMessage();
            }
        }

        return "请提供商品ID或商品名称关键词";
    }
}
