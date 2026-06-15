package edu.scau.mis.common.vo.dashboard;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Codex added: 数据库按天聚合后的趋势点，供 service 组装前端数组。
 */
@Data
public class TrendPointVo {
    private String dateLabel;
    private BigDecimal sales;
    private Integer orders;
}
