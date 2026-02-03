package edu.scau.mis.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.scau.mis.common.annotation.AutoFill;
import edu.scau.mis.product.domain.Payment;


public interface  IPaymentMapper extends BaseMapper<Payment> {
    /**
     * 新增支付对象
     * @param payment 支付信息
     * @return 结果
     */
    @AutoFill()
    int insertPayment(Payment payment);

    /**
     * 根据id查询支付对象
     * @param paymentId 支付ID
     * @return 支付对象
     */
    Payment selectPaymentById(Long paymentId);
}
