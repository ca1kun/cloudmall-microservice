package edu.scau.mis.cart.controller;

import edu.scau.mis.common.domain.OmsCartItem;
import edu.scau.mis.cart.service.CartService;
import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.common.domain.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ApiResult<String> add(@RequestBody OmsCartItem cartItem) {
        // 1. 获取当前用户
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getId();

        // 2. 调用服务
        cartService.addCart(userId, cartItem);
        
        return ApiResult.success("加购成功");
    }

    @GetMapping("/list")
    public ApiResult<List<OmsCartItem>> list() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ApiResult.success(cartService.list(loginUser.getUser().getId()));
    }

    @DeleteMapping("/delete/{productId}")
    public ApiResult<String> delete(@PathVariable("productId") Long productId) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        cartService.delete(loginUser.getUser().getId(), productId);
        return ApiResult.success("删除成功");
    }
    @DeleteMapping("/delete/batch")
    public ApiResult<String> deleteBatch(@RequestBody List<Long> productIds) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        cartService.deleteBatch(productIds, loginUser.getUser().getId());
        return ApiResult.success("批量删除成功");
    }

}