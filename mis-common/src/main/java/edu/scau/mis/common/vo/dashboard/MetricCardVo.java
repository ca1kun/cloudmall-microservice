package edu.scau.mis.common.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Codex added: 商家大屏核心指标卡片返回对象。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "商家大屏核心指标卡片")
public class MetricCardVo {
    private String label;
    private Number value;
    private String valueText;
    private Double trend;
    private String trendText;
    private String direction;
    private String subtext;
}
