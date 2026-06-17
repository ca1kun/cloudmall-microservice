package edu.scau.mis.marketing.controller;

import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.common.domain.SmsCoupon;
import edu.scau.mis.marketing.domain.SmsCouponHistory;
import edu.scau.mis.marketing.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/coupon/ai")
public class CouponAiController {

    @Autowired
    private CouponService couponService;

    @PostMapping("/claim")
    public ApiResult<String> claim(@RequestParam Long memberId, @RequestParam Long couponId) {
        SmsCoupon coupon = couponService.getById(couponId);
        if (coupon == null) return ApiResult.error("优惠券不存在");
        if (coupon.getCount() != null && coupon.getCount() <= 0) return ApiResult.error("优惠券已抢完");

        SmsCouponHistory history = new SmsCouponHistory();
        history.setCouponId(couponId);
        history.setMemberId(memberId);
        history.setUseStatus(0);
        history.setCreateTime(new Date());
        couponService.saveHistory(history);
        return ApiResult.success("优惠券领取成功");
    }
}
