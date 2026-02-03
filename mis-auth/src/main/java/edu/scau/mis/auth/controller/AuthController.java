package edu.scau.mis.auth.controller;

import edu.scau.mis.auth.dto.LoginDTO;
import edu.scau.mis.auth.service.SysLoginService;
import edu.scau.mis.common.domain.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private SysLoginService loginService;

    // 发送验证码接口
    @PostMapping("/code")
    public ApiResult<String> sendCode(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        loginService.sendCode(phone);
        return ApiResult.success("验证码已发送", null);
    }

    // 统一登录接口
    @PostMapping("/login")
    public ApiResult<Map<String, Object>> login(@RequestBody LoginDTO loginDto) {
        // 如果没传 loginType，默认当做 account 处理，或者前端必须传
        if (loginDto.getLoginType() == null) {
            loginDto.setLoginType("account");
        }
        return ApiResult.success(loginService.login(loginDto));
    }
    @PostMapping("/logout")
    public ApiResult<String> logout() {
        loginService.logout();
        return ApiResult.success("退出成功");
    }
}