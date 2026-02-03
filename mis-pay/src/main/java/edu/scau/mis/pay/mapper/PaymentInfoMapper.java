package edu.scau.mis.pay.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.scau.mis.pay.domain.PaymentInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PaymentInfoMapper extends BaseMapper<PaymentInfo> {
}