package edu.scau.mis.common.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单表 实体类
 *
 * @author Gemini Code Assist
 */
@Data
@TableName("oms_order")
public class OmsOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 会员ID
     */
    private Long memberId;

    /**
     * 订单编号
     */
    private String orderSn;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 会员用户名
     */
    private String memberUsername;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 应付金额
     */
    private BigDecimal payAmount;

    /**
     * 订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭
     */
    private Integer status;

    /**
     * 订单备注
     */
    private String note;

    // 👇 新增字段：优惠金额
    private BigDecimal couponAmount;
}
