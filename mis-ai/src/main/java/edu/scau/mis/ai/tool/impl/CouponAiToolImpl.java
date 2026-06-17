package edu.scau.mis.ai.tool.impl;

import edu.scau.mis.ai.dto.CouponQueryDTO;
import edu.scau.mis.ai.feign.RemoteMarketingFeign;
import edu.scau.mis.ai.tool.CouponAiTool;
import edu.scau.mis.common.domain.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CouponAiToolImpl implements CouponAiTool {

    @Autowired
    private RemoteMarketingFeign remoteMarketingFeign;

    @Override
    public List<CouponQueryDTO> queryAvailableCoupons(Long memberId) {
        if (memberId == null) return new ArrayList<>();
        ApiResult<List<Map<String, Object>>> result = remoteMarketingFeign.getAvailableCoupons(memberId);
        List<CouponQueryDTO> list = new ArrayList<>();
        if (result == null || result.getData() == null) return list;
        for (Map<String, Object> item : result.getData()) {
            CouponQueryDTO dto = new CouponQueryDTO();
            dto.setCouponId(item.get("couponId") == null ? null : Long.valueOf(String.valueOf(item.get("couponId"))));
            dto.setCouponName(String.valueOf(item.getOrDefault("couponName", "")));
            if (item.get("amount") != null) dto.setAmount(new BigDecimal(String.valueOf(item.get("amount"))));
            dto.setUseStatus(item.get("useStatus") == null ? null : Integer.valueOf(String.valueOf(item.get("useStatus"))));
            dto.setEndTime(String.valueOf(item.getOrDefault("endTime", "")));
            list.add(dto);
        }
        return list;
    }

    @Override
    public String claimCoupon(Long memberId, Long couponId) {
        ApiResult<String> result = remoteMarketingFeign.claimCoupon(memberId, couponId);
        return result == null ? "领取失败" : result.getMessage();
    }
}
