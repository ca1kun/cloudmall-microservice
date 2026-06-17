package edu.scau.mis.ai.tool;

import edu.scau.mis.ai.dto.CouponQueryDTO;

import java.util.List;

public interface CouponAiTool {
    List<CouponQueryDTO> queryAvailableCoupons(Long memberId);
    String claimCoupon(Long memberId, Long couponId);
}
