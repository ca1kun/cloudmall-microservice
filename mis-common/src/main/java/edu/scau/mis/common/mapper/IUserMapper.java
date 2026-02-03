package edu.scau.mis.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.scau.mis.common.domain.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IUserMapper extends BaseMapper<SysUser> {
}