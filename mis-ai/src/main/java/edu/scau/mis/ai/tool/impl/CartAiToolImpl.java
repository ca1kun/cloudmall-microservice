package edu.scau.mis.ai.tool.impl;

import edu.scau.mis.ai.dto.IntentDetectResult;
import edu.scau.mis.ai.feign.RemoteCartFeign;
import edu.scau.mis.ai.feign.RemoteProductFeign;
import edu.scau.mis.ai.tool.CartAiTool;
import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.common.domain.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CartAiToolImpl implements CartAiTool {

    @Autowired
    private RemoteCartFeign remoteCartFeign;

    @Autowired
    private RemoteProductFeign remoteProductFeign;

    @Override
    public Map<String, Object> queryCartSummary(Long memberId) {
        if (memberId == null) return new HashMap<>();
        try {
            ApiResult<Map<String, Object>> result = remoteCartFeign.getCartSummary(memberId);
            return result == null || result.getData() == null ? new HashMap<>() : result.getData();
        } catch (Exception e) {
            log.warn("queryCartSummary failed, memberId={}", memberId, e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("error", "查询购物车失败: " + e.getMessage());
            return errorResult;
        }
    }

    @Override
    public String addToCart(Long memberId, Long productId, Integer quantity) {
        if (memberId == null) return "请先登录后再操作购物车";
        if (productId == null) return "未找到对应商品，请提供更具体的商品名称";
        try {
            ApiResult<String> result = remoteCartFeign.addToCart(memberId, productId, quantity);
            return result == null ? "添加失败" : result.getMessage();
        } catch (Exception e) {
            log.warn("addToCart failed, memberId={}, productId={}", memberId, productId, e);
            return "添加购物车失败: " + e.getMessage();
        }
    }

    @Override
    public String removeFromCart(Long memberId, Long productId) {
        if (memberId == null) return "请先登录后再操作购物车";
        if (productId == null) return "未找到对应商品";
        try {
            ApiResult<String> result = remoteCartFeign.removeFromCart(memberId, productId);
            return result == null ? "移除失败" : result.getMessage();
        } catch (Exception e) {
            log.warn("removeFromCart failed, memberId={}, productId={}", memberId, productId, e);
            return "移除购物车失败: " + e.getMessage();
        }
    }

    /**
     * 通过关键词从购物车中查找并移除商品
     * 优先在购物车已有商品中按名称匹配，匹配不到再从商品库搜索
     */
    @Override
    public String removeFromCartByKeyword(Long memberId, String keyword) {
        log.info("removeFromCartByKeyword called, memberId={}, keyword={}", memberId, keyword);
        if (memberId == null) return "请先登录后再操作购物车";
        if (keyword == null || keyword.isBlank()) return "请告诉我您想移除的商品名称";

        try {
            // 先查购物车，在已有商品中匹配关键词
            Map<String, Object> cartSummary = queryCartSummary(memberId);
            if (cartSummary != null && !cartSummary.isEmpty() && cartSummary.containsKey("items")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> items = (List<Map<String, Object>>) cartSummary.get("items");
                for (Map<String, Object> item : items) {
                    String productName = item.get("productName") != null ? item.get("productName").toString() : "";
                    if (productName.contains(keyword)) {
                        Object productIdObj = item.get("productId");
                        if (productIdObj != null) {
                            Long productId = Long.valueOf(productIdObj.toString());
                            log.info("Found in cart: productName={}, productId={}", productName, productId);
                            return removeFromCart(memberId, productId);
                        }
                    }
                }
            }

            // 购物车中未匹配到，再从商品库搜索
            ApiResult<List<Product>> searchResult = remoteProductFeign.searchProducts(keyword, null, null, null);
            log.info("searchProducts fallback result: code={}, dataSize={}", searchResult == null ? null : searchResult.getCode(),
                    searchResult == null || searchResult.getData() == null ? 0 : searchResult.getData().size());
            if (searchResult == null || searchResult.getData() == null || searchResult.getData().isEmpty()) {
                return "购物车中未找到「" + keyword + "」相关商品";
            }
            Product product = searchResult.getData().get(0);
            Long productId = product.getProductId();
            log.info("Found product via search: id={}, name={}", productId, product.getProductName());
            return removeFromCart(memberId, productId);
        } catch (Exception e) {
            log.warn("removeFromCartByKeyword failed, memberId={}, keyword={}", memberId, keyword, e);
            return "移除购物车失败: " + e.getMessage();
        }
    }

    /**
     * 通过关键词查找商品并加入购物车
     */
    public String addToCartByKeyword(Long memberId, String keyword, Integer quantity) {
        log.info("addToCartByKeyword called, memberId={}, keyword={}, quantity={}", memberId, keyword, quantity);
        if (memberId == null) return "请先登录后再操作购物车";
        if (keyword == null || keyword.isBlank()) return "请告诉我您想加入购物车的商品名称";

        try {
            // 先搜索商品获取 productId
            ApiResult<List<Product>> searchResult = remoteProductFeign.searchProducts(keyword, null, null, null);
            log.info("searchProducts result: code={}, dataSize={}", searchResult == null ? null : searchResult.getCode(),
                    searchResult == null || searchResult.getData() == null ? 0 : searchResult.getData().size());
            if (searchResult == null || searchResult.getData() == null || searchResult.getData().isEmpty()) {
                return "未找到商品「" + keyword + "」，请尝试更具体的名称";
            }
            Product product = searchResult.getData().get(0);
            Long productId = product.getProductId();
            log.info("Found product: id={}, name={}", productId, product.getProductName());
            return addToCart(memberId, productId, quantity);
        } catch (Exception e) {
            log.warn("addToCartByKeyword failed, memberId={}, keyword={}", memberId, keyword, e);
            return "添加购物车失败: " + e.getMessage();
        }
    }
}
