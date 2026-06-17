package edu.scau.mis.product.service;

import edu.scau.mis.product.vo.dashboard.CategoryShareVo;
import edu.scau.mis.product.vo.dashboard.ChannelOrdersVo;
import edu.scau.mis.product.vo.dashboard.DashboardOverviewVo;
import edu.scau.mis.product.vo.dashboard.MetricCardVo;
import edu.scau.mis.product.vo.dashboard.RecentOrdersVo;
import edu.scau.mis.product.vo.dashboard.TopProductsVo;
import edu.scau.mis.product.vo.dashboard.TrendDataVo;

import java.util.Map;

/**
 * Codex added: 商家大屏服务接口，负责根据数据库实体聚合 dashboard 数据。
 */
public interface IDashboardService {

    /**
     * Codex added: 获取核心指标卡片。
     */
    Map<String, MetricCardVo> getMetrics();

    /**
     * Codex added: 获取销售与订单趋势。
     */
    TrendDataVo getTrend(Integer days);

    /**
     * Codex added: 获取品类占比。
     */
    CategoryShareVo getCategoryShare();

    /**
     * Codex added: 获取渠道订单统计。
     */
    ChannelOrdersVo getChannelOrders(String period);

    /**
     * Codex added: 获取热销商品排行。
     */
    TopProductsVo getTopProducts(Integer limit, String period);

    /**
     * Codex added: 获取最新订单。
     */
    RecentOrdersVo getRecentOrders(Integer limit);

    /**
     * Codex added: 获取 dashboard 聚合数据。
     */
    DashboardOverviewVo getOverview(Integer trendDays, Integer topLimit, Integer recentLimit);
}
