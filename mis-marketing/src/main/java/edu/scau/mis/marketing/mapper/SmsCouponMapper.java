package edu.scau.mis.marketing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.scau.mis.common.domain.SmsCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SmsCouponMapper extends BaseMapper<SmsCoupon> {
    /**
     * 扣减库存 (关键：在 SQL 里判断 count > 0)
     *
     * @param id 优惠券ID
     */
    @Update("UPDATE sms_coupon SET count = count - 1 WHERE id = #{id} AND count > 0")
    void decreaseStock(@Param("id") Long id);

}