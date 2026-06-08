package edu.scau.mis.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import edu.scau.mis.product.domain.OmsMemberAddress;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员收货地址 Mapper
 */
@Mapper
public interface OmsMemberAddressMapper extends BaseMapper<OmsMemberAddress> {
}
