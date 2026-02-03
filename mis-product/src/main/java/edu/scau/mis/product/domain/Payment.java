package edu.scau.mis.product.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import edu.scau.mis.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("pos_payment") // <--- 关键：在这里明确指定数据库中的表名
public class Payment extends BaseEntity implements Serializable {
    @TableId(type = IdType.AUTO) // <--- 关键：添加这个注解
    private Long paymentId;
    private String paymentNo;
    private Long saleId;
    private BigDecimal amount;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date payTime;
    private String payMethod;
}
