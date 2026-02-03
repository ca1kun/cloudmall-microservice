package edu.scau.mis.common.dto;
import lombok.Data;

@Data
public class UserPwdDto {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword; // 前端传过来做校验，或者后端校验
}