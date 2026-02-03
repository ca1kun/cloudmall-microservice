package edu.scau.mis.auth.dto;

import lombok.Data;

@Data
public class LoginDTO {
    /**
     * 登录方式：
     * "account" = 账号密码登录
     * "sms"     = 手机验证码登录
     */
    private String loginType; 

    private String username; // 账号
    private String password; // 密码
    
    private String phone;    // 手机号
    private String code;     // 验证码
}