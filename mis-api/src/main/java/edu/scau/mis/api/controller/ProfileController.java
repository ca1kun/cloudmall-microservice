package edu.scau.mis.api.controller;

import edu.scau.mis.common.domain.SysUser;
import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.common.dto.UserProfileDto;
import edu.scau.mis.common.dto.UserPwdDto;
import edu.scau.mis.product.service.impl.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/user/profile")
public class ProfileController {

    @Autowired
    private UserProfileService profileService;

    // 获取信息
    @GetMapping
    public ApiResult<SysUser> getProfile() {
        return ApiResult.success(profileService.getProfile());
    }

    // 修改资料
    @PutMapping
    public ApiResult<String> updateProfile(@RequestBody UserProfileDto dto) {
        profileService.updateProfile(dto);
        return ApiResult.success("资料更新成功");
    }

    // 修改密码
    @PutMapping("/password")
    public ApiResult<String> updatePassword(@RequestBody UserPwdDto dto) {
        profileService.updatePassword(dto);
        return ApiResult.success("密码修改成功，下次登录生效");
    }
}