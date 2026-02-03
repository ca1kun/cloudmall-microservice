package edu.scau.mis.common.dto;

import lombok.Data;
@Data
public class LoginUserDTO {
    private String avatar;
    private String userName;
    private String password;
    private String token;
}