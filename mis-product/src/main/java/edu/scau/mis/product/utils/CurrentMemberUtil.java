package edu.scau.mis.product.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 当前登录会员工具类。
 *
 * 注意：
 * 1. 这里为了方便你现在直接联调，支持从请求头 X-Member-Id / memberId 读取会员ID。
 * 2. 如果请求头没有传，默认使用 2L，因为你当前订单/购物车示例数据里 member_id 多数为 2。
 * 3. 后续接入你项目自己的 JWT 登录后，建议把 getCurrentMemberId() 改成从 token / SecurityContext 中读取。
 */
@Component
public class CurrentMemberUtil {

    private static final Long DEV_DEFAULT_MEMBER_ID = 2L;

    public Long getCurrentMemberId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return DEV_DEFAULT_MEMBER_ID;
        }

        HttpServletRequest request = attributes.getRequest();

        Object attrMemberId = request.getAttribute("memberId");
        Long attrValue = parseLong(attrMemberId);
        if (attrValue != null) {
            return attrValue;
        }

        String headerMemberId = request.getHeader("X-Member-Id");
        if (!StringUtils.hasText(headerMemberId)) {
            headerMemberId = request.getHeader("memberId");
        }

        Long headerValue = parseLong(headerMemberId);
        return headerValue == null ? DEV_DEFAULT_MEMBER_ID : headerValue;
    }

    private Long parseLong(Object value) {
        if (value == null) {
            return null;
        }
        try {
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }
            String text = String.valueOf(value);
            if (!StringUtils.hasText(text)) {
                return null;
            }
            return Long.parseLong(text);
        } catch (Exception e) {
            return null;
        }
    }
}
