package edu.scau.mis.order.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Method;

public class CurrentMemberUtil {
    private CurrentMemberUtil() {}

    public static Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return 2L;
        Long id = tryReadId(authentication.getPrincipal());
        if (id != null) return id;
        id = tryReadId(authentication.getDetails());
        if (id != null) return id;
        String name = authentication.getName();
        if (name != null && name.matches("\\d+")) return Long.valueOf(name);
        return 2L;
    }

    private static Long tryReadId(Object target) {
        if (target == null) return null;
        if (target instanceof Long) return (Long) target;
        if (target instanceof Integer) return ((Integer) target).longValue();
        if (target instanceof String && ((String) target).matches("\\d+")) return Long.valueOf((String) target);
        String[] methodNames = {"getUserId", "getId", "getMemberId"};
        for (String methodName : methodNames) {
            try {
                Method method = target.getClass().getMethod(methodName);
                Long id = tryReadId(method.invoke(target));
                if (id != null) return id;
            } catch (Exception ignored) {}
        }
        try {
            Method getUser = target.getClass().getMethod("getUser");
            return tryReadId(getUser.invoke(target));
        } catch (Exception ignored) {}
        return null;
    }
}
