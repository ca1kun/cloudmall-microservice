package edu.scau.mis.common.dto;

import lombok.Data;

@Data
public class LoginBodyDTO {
    private String username;
    private String password;
    private String phone;    // 手机号
    private String code;     // 验证码
}