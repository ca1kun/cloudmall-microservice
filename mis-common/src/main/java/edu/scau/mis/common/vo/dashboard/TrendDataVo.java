package edu.scau.mis.common.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Codex added: 销售与订单趋势返回对象。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "销售与订单趋势")
public class TrendDataVo {
    private List<String> days;
    private List<BigDecimal> sales;
    private List<Integer> orders;
    private List<BigDecimal> averageOrderValue;
}
