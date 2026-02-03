package edu.scau.mis.common.dto;
import lombok.Data;

@Data
public class UserProfileDto {
    private String nickname; // 昵称（如果有的话，没有可以用 username）
    private String phone;
    private String avatar;
    // 不要放 username，因为用户名通常不允许修改
}