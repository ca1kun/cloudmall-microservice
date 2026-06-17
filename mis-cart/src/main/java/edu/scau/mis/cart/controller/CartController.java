package edu.scau.mis.cart.controller;

import edu.scau.mis.cart.service.CartService;
import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.common.domain.LoginUser;
import edu.scau.mis.common.domain.OmsCartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ApiResult<String> add(@RequestBody OmsCartItem cartItem) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getId();
        cartService.addCart(userId, cartItem);
        return ApiResult.success("加购成功", null);
    }

    @GetMapping("/list")
    public ApiResult<List<OmsCartItem>> list() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResult.success(cartService.list(loginUser.getUser().getId()));
    }

    @GetMapping("/ai/summary")
    public ApiResult<Map<String, Object>> getCartSummary(@RequestParam("memberId") Long memberId) {
        List<OmsCartItem> cartItems = cartService.list(memberId);
        Map<String, Object> summary = new HashMap<>();
        summary.put("memberId", memberId);
        summary.put("itemCount", cartItems.size());
        summary.put("totalQuantity", cartItems.stream().mapToInt(item -> item.getQuantity() == null ? 0 : item.getQuantity()).sum());
        summary.put("totalAmount", cartItems.stream()
                .map(item -> {
                    BigDecimal price = item.getCurrentPrice() != null ? item.getCurrentPrice() : item.getPrice();
                    int quantity = item.getQuantity() == null ? 0 : item.getQuantity();
                    return price == null ? BigDecimal.ZERO : price.multiply(BigDecimal.valueOf(quantity));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        summary.put("items", cartItems);
        return ApiResult.success(summary);
    }

    @DeleteMapping("/delete/{productId}")
    public ApiResult<String> delete(@PathVariable("productId") Long productId) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        cartService.delete(loginUser.getUser().getId(), productId);
        return ApiResult.success("删除成功", null);
    }

    @DeleteMapping("/delete/batch")
    public ApiResult<String> deleteBatch(@RequestBody List<Long> productIds) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        cartService.deleteBatch(productIds, loginUser.getUser().getId());
        return ApiResult.success("批量删除成功", null);
    }

    // ========== AI 内部调用接口（无需登录态，由 mis-ai Feign 调用） ==========

    @PostMapping("/ai/add")
    public ApiResult<String> aiAdd(@RequestParam("memberId") Long memberId,
                                   @RequestParam("productId") Long productId,
                                   @RequestParam(value = "quantity", defaultValue = "1") Integer quantity) {
        OmsCartItem item = new OmsCartItem();
        item.setMemberId(memberId);
        item.setProductId(productId);
        item.setQuantity(quantity);
        cartService.addCart(memberId, item);
        return ApiResult.success("已加入购物车");
    }

    @DeleteMapping("/ai/remove")
    public ApiResult<String> aiRemove(@RequestParam("memberId") Long memberId,
                                      @RequestParam("productId") Long productId) {
        cartService.delete(memberId, productId);
        return ApiResult.success("已从购物车移除");
    }
}
