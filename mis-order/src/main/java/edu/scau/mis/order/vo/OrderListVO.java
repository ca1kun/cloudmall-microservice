package edu.scau.mis.order.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderListVO {
    private Long orderId;
    private String orderNo;
    private String status;
    @JsonIgnore
    private Integer statusCode;
    private BigDecimal totalAmount;
    private BigDecimal payAmount;
    private Integer totalQuantity;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    private String buyerName;
    private String buyerPhone;
}
