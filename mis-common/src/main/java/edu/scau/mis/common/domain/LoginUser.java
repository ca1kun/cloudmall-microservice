package edu.scau.mis.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnore; // 👈 导入这个
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
// 👇 【建议加上这个】兜底策略：如果 JSON 里有未知的字段，直接忽略，不要报错
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginUser implements UserDetails {

    private SysUser user;

    public LoginUser(SysUser user) {
        this.user = user;
    }

    // ============================================================
    // 👇 给下面这些方法全部加上 @JsonIgnore
    //    告诉 Redis：存数据的时候，别把这些存进去！
    // ============================================================

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(user != null && user.getRole() != null) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
        }
        return Collections.emptyList();
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return "0".equals(user.getStatus());
    }

    // 重写 toString 避免死循环 (保持你之前的修改)
    @Override
    public String toString() {
        return "LoginUser{userId=" + (user != null ? user.getId() : "null") + "}";
    }
}