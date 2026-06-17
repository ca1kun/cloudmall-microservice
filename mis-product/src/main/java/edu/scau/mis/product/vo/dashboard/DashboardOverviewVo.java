package edu.scau.mis.product.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Codex added: 商家大屏聚合返回对象。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "商家大屏聚合数据")
public class DashboardOverviewVo {
    private Map<String, MetricCardVo> metrics;
    private TrendDataVo trend;
    private CategoryShareVo categoryShare;
    private ChannelOrdersVo channelOrders;
    private TopProductsVo topProducts;
    private RecentOrdersVo recentOrders;
}
