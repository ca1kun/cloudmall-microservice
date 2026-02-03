package edu.scau.mis.marketing.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.scau.mis.common.domain.LoginUser;
import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.marketing.domain.CouponHistoryDetail;
import edu.scau.mis.common.domain.SmsCoupon;
import edu.scau.mis.marketing.domain.SmsCouponHistory;
import edu.scau.mis.marketing.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/coupon")
public class CouponController {

    @Autowired
    private CouponService couponService;

    @PostMapping("/seckill/{id}")
    public ApiResult<String> secKill(@PathVariable("id") Long couponId) {
        // 获取当前登录用户
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getId();
        String username = loginUser.getUsername();

        couponService.secKillCoupon(couponId, userId, username);
        return ApiResult.success("抢券成功");
    }
    /**
     * 预热接口 (通常只有管理员能调用)
     * POST /coupon/preheat/1
     */
    @PostMapping("/preheat/{id}")
    public ApiResult<String> preHeat(@PathVariable("id") Long couponId) {
        couponService.preHeat(couponId);
        return ApiResult.success("库存预热成功");
    }
    @GetMapping("/list")
    public ApiResult<List<SmsCoupon>> list() {
        // 简单查所有 (实际业务可能要查未过期、未领完的)
        // 建议加个条件：endTime > now()
        List<SmsCoupon> list = couponService.list(
                new LambdaQueryWrapper<SmsCoupon>()
                        .gt(SmsCoupon::getEndTime, new Date()) // 未过期
                        .gt(SmsCoupon::getCount, 0) // 有库存
        );
        return ApiResult.success(list);
    }
    // 获取我的详细优惠券列表
    @GetMapping("/my/ids")
    public ApiResult<List<CouponHistoryDetail>> getMyCouponList() {
        System.out.println("进入 /my/ids 接口，当前用户: " + SecurityContextHolder.getContext().getAuthentication());
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getId();

        // 👇 直接调用 Service 封装好的方法
        List<CouponHistoryDetail> list = couponService.listMyCoupons(userId);
        return ApiResult.success(list);
    }

    // 1. 查询优惠券详情 (供 mis-order 计算价格)
    @GetMapping("/info/{id}")
    public ApiResult<SmsCoupon> getCouponInfo(@PathVariable("id") Long couponId) {
        return ApiResult.success(couponService.getById(couponId));
    }

    // 2. 核销优惠券 (下单成功后调用)
    @PostMapping("/use")
    public ApiResult<String> useCoupon(@RequestParam Long couponId, @RequestParam Long orderId) {
        // 根据 couponId 和 当前用户 查找那张未使用的券
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = loginUser.getUser().getId();

        // 调用 service 层方法完成核销操作
        couponService.useCoupon(couponId, userId, orderId);

        return ApiResult.success("核销成功");
    }
    @PostMapping("/add")
    // @PreAuthorize("hasRole('ADMIN')") // 最好加上权限控制
    public ApiResult<String> add(@RequestBody SmsCoupon coupon) {
        // 1. 设置默认值
        if (coupon.getMinPoint() == null) coupon.setMinPoint(BigDecimal.ZERO);
        if (coupon.getPerLimit() == null) coupon.setPerLimit(1);

        // 2. 保存
        boolean success = couponService.save(coupon);
        return success ? ApiResult.success("添加成功") : ApiResult.error("添加失败");
    }
    @GetMapping("/page")
    public ApiResult<IPage<SmsCoupon>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                            @RequestParam(defaultValue = "10") Integer pageSize) {
        IPage<SmsCoupon> page = new Page<>(pageNum, pageSize);
        couponService.page(page, new LambdaQueryWrapper<SmsCoupon>().orderByDesc(SmsCoupon::getId));
        return ApiResult.success(page);
    }
}