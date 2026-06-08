package edu.scau.mis.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import edu.scau.mis.common.domain.LoginUser;
import edu.scau.mis.common.domain.SysUser;
import edu.scau.mis.common.mapper.IUserMapper;
import edu.scau.mis.common.utils.JwtUtil;
import edu.scau.mis.common.utils.RedisCache;
import edu.scau.mis.common.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.aliyun.dypnsapi20170525.Client;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeRequest;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeResponse;
import com.aliyun.dypnsapi20170525.models.CheckSmsVerifyCodeRequest;
import com.aliyun.dypnsapi20170525.models.CheckSmsVerifyCodeResponse;
import edu.scau.mis.auth.config.AliyunPnvsProperties;

import edu.scau.mis.auth.dto.LoginDTO;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class SysLoginService {


    @Autowired
    private Client aliyunPnvsClient;

    @Autowired
    private AliyunPnvsProperties aliyunPnvsProperties;

    @Autowired
    private IUserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RedisCache redisCache;

    // Redis Key 前缀
    private static final String CAPTCHA_KEY_PREFIX = "captcha:";

    /**
     * 发送验证码：使用阿里云短信认证服务
     */
    public void sendCode(String phone) {
        if (!StringUtils.hasText(phone) || !phone.matches("^1[3-9]\\d{9}$")) {
            throw new ServiceException("手机号格式不正确");
        }

        try {
            SendSmsVerifyCodeRequest request = new SendSmsVerifyCodeRequest()
                    .setPhoneNumber(phone)
                    .setCountryCode("86")
                    .setSignName(aliyunPnvsProperties.getSignName())
                    .setTemplateCode(aliyunPnvsProperties.getTemplateCode())
                    .setSchemeName(aliyunPnvsProperties.getSchemeName())
                    // 这里让阿里云生成验证码，后面才能用 CheckSmsVerifyCode 校验
                    .setTemplateParam("{\"code\":\"##code##\",\"min\":\"1\"}")
                    .setCodeLength(Long.valueOf(aliyunPnvsProperties.getCodeLength()))
                    .setValidTime(Long.valueOf(aliyunPnvsProperties.getValidTime()))
                    .setInterval(Long.valueOf(aliyunPnvsProperties.getInterval()))
                    .setCodeType(1L)
                    .setDuplicatePolicy(1L);

            SendSmsVerifyCodeResponse response = aliyunPnvsClient.sendSmsVerifyCode(request);

            if (response == null || response.getBody() == null) {
                throw new ServiceException("验证码发送失败：阿里云无响应");
            }

            String resultCode = response.getBody().getCode();
            Boolean success = response.getBody().getSuccess();
            String message = response.getBody().getMessage();

            if (!"OK".equals(resultCode) || !Boolean.TRUE.equals(success)) {
                throw new ServiceException("验证码发送失败：" + message);
            }

        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("验证码发送异常：" + e.getMessage());
        }
    }

    /**
     * 统一登录接口
     */
    public Map<String, Object> login(LoginDTO loginBody) {
        SysUser user = null;

        // ================= 分支 A：手机验证码登录 (默认) =================
        if ("sms".equals(loginBody.getLoginType())) {
            String phone = loginBody.getPhone();
            String code = loginBody.getCode();

            // 1. 校验手机号和验证码
            if (!StringUtils.hasText(phone) || !phone.matches("^1[3-9]\\d{9}$")) {
                throw new ServiceException("手机号格式不正确");
            }
            if (!StringUtils.hasText(code)) {
                throw new ServiceException("验证码不能为空");
            }

            try {
                CheckSmsVerifyCodeRequest checkRequest = new CheckSmsVerifyCodeRequest()
                        .setPhoneNumber(phone)
                        .setCountryCode("86")
                        .setSchemeName(aliyunPnvsProperties.getSchemeName())
                        .setVerifyCode(code);

                CheckSmsVerifyCodeResponse checkResponse = aliyunPnvsClient.checkSmsVerifyCode(checkRequest);

                if (checkResponse == null || checkResponse.getBody() == null) {
                    throw new ServiceException("验证码校验失败：阿里云无响应");
                }

                String resultCode = checkResponse.getBody().getCode();
                Boolean success = checkResponse.getBody().getSuccess();

                if (!"OK".equals(resultCode) || !Boolean.TRUE.equals(success)) {
                    throw new ServiceException("验证码校验失败：" + checkResponse.getBody().getMessage());
                }

                String verifyResult = checkResponse.getBody().getModel() == null
                        ? null
                        : checkResponse.getBody().getModel().getVerifyResult();

                if (!"PASS".equals(verifyResult)) {
                    throw new ServiceException("验证码错误或已失效");
                }

            } catch (ServiceException e) {
                throw e;
            } catch (Exception e) {
                throw new ServiceException("验证码校验异常：" + e.getMessage());
            }

            // 3. 查库：根据手机号查用户
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getPhone, phone);
            user = userMapper.selectOne(wrapper);

            // 4. 自动注册：如果用户不存在
            if (user == null) {
                user = new SysUser();
                user.setUsername(phone); // 用户名默认设为手机号
                user.setPhone(phone);
                user.setRole("CUSTOMER"); // 默认角色
                user.setStatus("0");
                // 设置一个随机初始密码，防止报错，并加密
                user.setPassword(passwordEncoder.encode("init@123456"));
                userMapper.insert(user);
                System.out.println("✅ 新用户 " + phone + " 自动注册成功");
            }
        }

        // ================= 分支 B：账号密码登录 (备选) =================
        else {
            String username = loginBody.getUsername();
            String password = loginBody.getPassword();

            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getUsername, username);
            user = userMapper.selectOne(wrapper);

            if (Objects.isNull(user)) {
                throw new ServiceException("登录失败：用户不存在");
            }
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new ServiceException("登录失败：密码错误");
            }
        }

        // ================= 公共逻辑：生成 Token =================
        LoginUser loginUser = new LoginUser(user);
        String userId = user.getId().toString();

        // 存入 Redis
        redisCache.setCacheObject("login:" + userId, loginUser, 30, TimeUnit.MINUTES);

        // 返回结果
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("token", jwtUtil.createJWT(userId));
        resultMap.put("role", user.getRole());
        resultMap.put("username", user.getUsername());
        resultMap.put("avatar", user.getAvatar());

        return resultMap;
    }
    /**
     * 退出登录
     * 核心逻辑：获取当前登录用户ID -> 删除 Redis 缓存
     */
    public void logout() {
        // 1. 从 Spring Security 上下文中获取当前认证信息
        // 因为请求经过了 JwtAuthenticationTokenFilter，所以这里一定能取到 LoginUser
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof LoginUser loginUser) {
            String userId = loginUser.getUser().getId().toString();

            // 2. 删除 Redis 中的缓存
            redisCache.deleteObject("login:" + userId);

            System.out.println("用户 " + loginUser.getUsername() + " 已退出，Redis缓存已清除。");
        }
    }
}