package edu.scau.mis.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import edu.scau.mis.common.domain.LoginUser;
import edu.scau.mis.common.domain.SysUser;
import edu.scau.mis.common.mapper.IUserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final IUserMapper userMapper; // 只依赖 Mapper，不要依赖其他 Service

    public UserDetailsServiceImpl(IUserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("====== [调试] 正在查询用户名: " + username + " ======");

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser user = userMapper.selectOne(wrapper);

        if (Objects.isNull(user)) {
            System.err.println("====== [调试] 数据库没查到这个人 ======");
            throw new UsernameNotFoundException("用户不存在");
        }

        System.out.println("====== [调试] 数据库查到了用户 ID: " + user.getId());
        System.out.println("====== [调试] 数据库里的密码(Hash): " + user.getPassword());
        System.out.println("====== [调试] 密码长度: " + (user.getPassword() == null ? 0 : user.getPassword().length()));

        return new LoginUser(user);
    }
}