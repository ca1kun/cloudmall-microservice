package edu.scau.mis.ai.function;

import edu.scau.mis.ai.feign.RemoteMarketingFeign;
import edu.scau.mis.common.domain.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 优惠券工具 - 供 LLM 通过 Tool Calling 自主调用
 */
@Slf4j
@Component
public class CouponTools {

    private final RemoteMarketingFeign remoteMarketingFeign;

    public CouponTools(RemoteMarketingFeign remoteMarketingFeign) {
        this.remoteMarketingFeign = remoteMarketingFeign;
    }

    @Tool(description = "查询用户可用的优惠券列表。返回优惠券名称、金额、使用状态和到期时间。")
    public String queryCoupons(
            @ToolParam(description = "用户ID") Long memberId
    ) {
        log.info("queryCoupons called: memberId={}", memberId);
        if (memberId == null) return "请先登录";
        try {
            ApiResult<List<Map<String, Object>>> result = remoteMarketingFeign.getAvailableCoupons(memberId);
            if (result == null || result.getData() == null || result.getData().isEmpty()) {
                return "暂无可用优惠券";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("可用优惠券：\n");
            for (Map<String, Object> item : result.getData()) {
                String couponId = item.get("couponId") != null ? String.valueOf(item.get("couponId")) : "";
                String couponName = String.valueOf(item.getOrDefault("couponName", ""));
                String amount = item.get("amount") != null ? String.valueOf(item.get("amount")) : "";
                String endTime = String.valueOf(item.getOrDefault("endTime", ""));
                sb.append("- 优惠券ID: ").append(couponId)
                  .append(", 名称: ").append(couponName)
                  .append(", 金额: ").append(amount)
                  .append(", 到期时间: ").append(endTime)
                  .append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            log.warn("queryCoupons failed", e);
            return "查询优惠券失败: " + e.getMessage();
        }
    }

    @Tool(description = "领取优惠券。根据优惠券ID领取。")
    public String claimCoupon(
            @ToolParam(description = "用户ID") Long memberId,
            @ToolParam(description = "优惠券ID") Long couponId
    ) {
        log.info("claimCoupon called: memberId={}, couponId={}", memberId, couponId);
        if (memberId == null) return "请先登录";
        if (couponId == null) return "请提供优惠券ID";
        try {
            ApiResult<String> result = remoteMarketingFeign.claimCoupon(memberId, couponId);
            return result == null ? "领取失败" : result.getMessage();
        } catch (Exception e) {
            log.warn("claimCoupon failed", e);
            return "领取优惠券失败: " + e.getMessage();
        }
    }
}
