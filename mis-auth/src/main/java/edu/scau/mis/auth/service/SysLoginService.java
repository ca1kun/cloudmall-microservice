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
import edu.scau.mis.auth.dto.LoginDTO;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class SysLoginService {

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
     * 发送验证码
     */
    public void sendCode(String phone) {
        // 1. 简单校验手机号格式 (这里简单判断长度，生产环境用正则)
        if (!StringUtils.hasText(phone) || phone.length() != 11) {
            throw new RuntimeException("手机号格式不正确");
        }

        // 2. 防刷校验：检查 Redis 里是否还有未过期的验证码
        // 你的需求是 1 分钟过期，如果这就意味着 60s 内不允许重发
        String key = CAPTCHA_KEY_PREFIX + phone;
        if (redisCache.getCacheObject(key) != null) {
            throw new RuntimeException("验证码发送太频繁，请稍后再试");
        }

        // 3. 生成 6 位随机数
        String code = String.valueOf(new Random().nextInt(899999) + 100000);

        // 4. 存入 Redis，有效期 60 秒
        redisCache.setCacheObject(key, code, 60, TimeUnit.SECONDS);

        // 5. 模拟发送短信 (生产环境调用阿里云/腾讯云 API)
        System.out.println("====== 【短信网关】 ======");
        System.out.println("发送给: " + phone);
        System.out.println("验证码: " + code);
        System.out.println("有效期: 60秒");
        System.out.println("========================");
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

            // 1. 校验验证码
            String cacheCode = redisCache.getCacheObject(CAPTCHA_KEY_PREFIX + phone);
            if (cacheCode == null) {
                throw new RuntimeException("验证码已失效，请重新发送");
            }
            if (!code.equals(cacheCode)) {
                throw new RuntimeException("验证码错误");
            }

            // 2. 验证通过，删除验证码（防止二次使用）
            redisCache.deleteObject(CAPTCHA_KEY_PREFIX + phone);

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