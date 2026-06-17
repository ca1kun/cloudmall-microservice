package edu.scau.mis.marketing.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.common.domain.LoginUser;
import edu.scau.mis.common.domain.SmsCoupon;
import edu.scau.mis.marketing.domain.CouponHistoryDetail;
import edu.scau.mis.marketing.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
    @SentinelResource(
            value = "coupon-seckill",
            blockHandler = "secKillBlockHandler"
    )
    public ResponseEntity<ApiResult<String>> secKill(@PathVariable("id") Long couponId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser loginUser)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResult.fail("用户未登录"));
        }

        Long userId = loginUser.getUser().getId();
        String username = loginUser.getUsername();

        String result = couponService.secKillCoupon(couponId, userId, username);

        // 秒杀请求成功进入 MQ
        if (CouponService.SECKILL_SUCCESS_MSG.equals(result)) {
            return ResponseEntity.ok(ApiResult.success(result));
        }

        // 压测时建议业务失败也返回 200，避免 JMeter 把“已抢光/重复领取”算成系统异常
        return ResponseEntity.ok(ApiResult.fail(result));
    }

    public ResponseEntity<ApiResult<String>> secKillBlockHandler(
            Long couponId,
            BlockException ex
    ) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ApiResult.fail("当前抢券人数过多，请稍后再试"));
    }

    /**
     * 预热接口
     * POST /coupon/preheat/1
     */
    @PostMapping("/preheat/{id}")
    public ApiResult<String> preHeat(@PathVariable("id") Long couponId) {
        couponService.preHeat(couponId);
        return ApiResult.success("库存预热成功");
    }

    @GetMapping("/list")
    public ApiResult<List<SmsCoupon>> list() {
        List<SmsCoupon> list = couponService.list(
                new LambdaQueryWrapper<SmsCoupon>()
                        .gt(SmsCoupon::getEndTime, new Date())
                        .gt(SmsCoupon::getCount, 0)
        );
        return ApiResult.success(list);
    }

    @GetMapping("/my/ids")
    public ApiResult<List<CouponHistoryDetail>> getMyCouponList() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser loginUser)) {
            return ApiResult.fail("用户未登录");
        }

        Long userId = loginUser.getUser().getId();
        List<CouponHistoryDetail> list = couponService.listMyCoupons(userId);
        return ApiResult.success(list);
    }

    @GetMapping("/info/{id}")
    public ApiResult<SmsCoupon> getCouponInfo(@PathVariable("id") Long couponId) {
        return ApiResult.success(couponService.getById(couponId));
    }

    @PostMapping("/use")
    public ApiResult<String> useCoupon(@RequestParam Long couponId,
                                       @RequestParam Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser loginUser)) {
            return ApiResult.fail("用户未登录");
        }

        Long userId = loginUser.getUser().getId();
        couponService.useCoupon(couponId, userId, orderId);

        return ApiResult.success("核销成功");
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<String> add(@RequestBody SmsCoupon coupon) {
        if (coupon.getMinPoint() == null) {
            coupon.setMinPoint(BigDecimal.ZERO);
        }

        if (coupon.getPerLimit() == null) {
            coupon.setPerLimit(1);
        }

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