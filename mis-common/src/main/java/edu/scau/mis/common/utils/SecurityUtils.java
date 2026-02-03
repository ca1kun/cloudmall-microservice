package edu.scau.mis.common.utils; // 包路径自定义

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 安全服务工具类
 */
public class SecurityUtils {

    /**
     * 获取用户账户
     **/
    public static String getUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            } else if (principal instanceof String) {
                return (String) principal;
            }
            return null;
        } catch (Exception e) {
            // 在某些非用户请求的场景下（如定时任务），可能会获取不到，返回null
            return null;
        }
    }
}