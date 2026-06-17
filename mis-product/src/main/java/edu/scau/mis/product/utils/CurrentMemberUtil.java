package edu.scau.mis.product.utils;

import edu.scau.mis.common.domain.LoginUser;
import edu.scau.mis.common.domain.SysUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Current logged-in member helper.
 */
@Component
public class CurrentMemberUtil {

    public Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("用户未登录");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof LoginUser)) {
            throw new IllegalStateException("用户未登录");
        }

        SysUser user = ((LoginUser) principal).getUser();
        if (user == null || user.getId() == null) {
            throw new IllegalStateException("用户未登录");
        }
        return user.getId();
    }
}
