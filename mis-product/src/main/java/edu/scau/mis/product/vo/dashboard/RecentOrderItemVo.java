package edu.scau.mis.product.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Codex added: 最新订单返回项，由销售订单表读取。
 */
@Data
@Schema(description = "最新订单项")
public class RecentOrderItemVo {
    private String orderNo;
    private String customer;
    private BigDecimal amount;
    private String amountText;
    private String status;
    private String statusText;
    private String statusType;
    private String createTime;
}
