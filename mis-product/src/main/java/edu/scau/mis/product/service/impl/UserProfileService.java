package edu.scau.mis.product.service.impl;

import edu.scau.mis.common.domain.LoginUser;
import edu.scau.mis.common.domain.SysUser;
import edu.scau.mis.common.mapper.IUserMapper;
import edu.scau.mis.common.exception.ServiceException;
import edu.scau.mis.common.dto.UserProfileDto;
import edu.scau.mis.common.dto.UserPwdDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserProfileService {

    private final IUserMapper userMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserProfileService(IUserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 获取当前登录用户的 ID
     */
    private Long getCurrentUserId() {
        try {
            LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return loginUser.getUser().getId();
        } catch (Exception e) {
            throw new ServiceException("获取当前用户信息失败");
        }
    }

    /**
     * 获取个人信息
     */
    public SysUser getProfile() {
        return userMapper.selectById(getCurrentUserId());
    }

    /**
     * 更新基本资料
     */
    public void updateProfile(UserProfileDto dto) {
        Long userId = getCurrentUserId();
        SysUser user = new SysUser();
        user.setId(userId);
        user.setPhone(dto.getPhone());
        user.setAvatar(dto.getAvatar());
        // 如果你有 nickname 字段就 setNickname
        
        userMapper.updateById(user);
    }

    /**
     * 修改密码
     */
    public void updatePassword(UserPwdDto dto) {
        Long userId = getCurrentUserId();
        SysUser user = userMapper.selectById(userId);

        // 1. 校验旧密码是否正确
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new ServiceException("旧密码错误");
        }

        // 2. 校验新密码不能和旧密码一样（可选）
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new ServiceException("新密码不能与旧密码相同");
        }

        // 3. 加密新密码并保存
        String newHash = passwordEncoder.encode(dto.getNewPassword());
        user.setPassword(newHash);
        
        userMapper.updateById(user);
    }
}