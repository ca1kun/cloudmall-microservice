package edu.scau.mis.api.controller;

import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.product.vo.dashboard.CategoryShareVo;
import edu.scau.mis.product.vo.dashboard.ChannelOrdersVo;
import edu.scau.mis.product.vo.dashboard.DashboardOverviewVo;
import edu.scau.mis.product.vo.dashboard.MetricCardVo;
import edu.scau.mis.product.vo.dashboard.RecentOrdersVo;
import edu.scau.mis.product.vo.dashboard.TopProductsVo;
import edu.scau.mis.product.vo.dashboard.TrendDataVo;
import edu.scau.mis.product.service.IDashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Codex added: 商家大屏接口 Controller。
 * Controller 只接收请求参数，真实数据聚合由 mis-product 的 IDashboardService 完成。
 */
@RestController
@RequestMapping("/merchant/dashboard")
@Tag(name = "商家大屏")
public class MerchantDashboardController {

    @Autowired
    private IDashboardService dashboardService;

    /**
     * Codex added: 获取商家运营核心指标。
     */
    @GetMapping("/metrics")
    @Operation(summary = "获取商家大屏核心指标")
    public ApiResult<Map<String, MetricCardVo>> metrics() {
        return ApiResult.success(dashboardService.getMetrics());
    }

    /**
     * Codex added: 获取近 N 天销售额、订单数、客单价趋势。
     */
    @GetMapping("/trend")
    @Operation(summary = "获取销售与订单趋势")
    public ApiResult<TrendDataVo> trend(
            @Parameter(description = "查询天数，范围 3~30，默认 7")
            @RequestParam(value = "days", defaultValue = "7") Integer days) {
        return ApiResult.success(dashboardService.getTrend(days));
    }

    /**
     * Codex added: 获取按订单明细销量聚合的品类占比。
     */
    @GetMapping("/category-share")
    @Operation(summary = "获取品类占比")
    public ApiResult<CategoryShareVo> categoryShare() {
        return ApiResult.success(dashboardService.getCategoryShare());
    }

    /**
     * Codex added: 获取渠道订单统计；当前项目实体无渠道字段，service 按 POS 渠道聚合。
     */
    @GetMapping("/channel-orders")
    @Operation(summary = "获取渠道订单数据")
    public ApiResult<ChannelOrdersVo> channelOrders(
            @Parameter(description = "统计周期：week 本周 / month 本月")
            @RequestParam(value = "period", defaultValue = "week") String period) {
        return ApiResult.success(dashboardService.getChannelOrders(period));
    }

    /**
     * Codex added: 获取热销商品排行。
     */
    @GetMapping("/top-products")
    @Operation(summary = "获取热销商品排行")
    public ApiResult<TopProductsVo> topProducts(
            @Parameter(description = "返回条数，范围 5~20，默认 10")
            @RequestParam(value = "limit", defaultValue = "10") Integer limit,
            @Parameter(description = "统计周期：week 本周 / month 本月 / all 全部")
            @RequestParam(value = "period", defaultValue = "week") String period) {
        return ApiResult.success(dashboardService.getTopProducts(limit, period));
    }

    /**
     * Codex added: 获取最新订单。
     */
    @GetMapping("/recent-orders")
    @Operation(summary = "获取最新订单")
    public ApiResult<RecentOrdersVo> recentOrders(
            @Parameter(description = "返回条数，范围 5~20，默认 5")
            @RequestParam(value = "limit", defaultValue = "5") Integer limit) {
        return ApiResult.success(dashboardService.getRecentOrders(limit));
    }

    /**
     * Codex added: 一次性获取大屏所需聚合数据。
     */
    @GetMapping("/overview")
    @Operation(summary = "获取商家大屏聚合数据")
    public ApiResult<DashboardOverviewVo> overview(
            @RequestParam(value = "trendDays", defaultValue = "7") Integer trendDays,
            @RequestParam(value = "topLimit", defaultValue = "10") Integer topLimit,
            @RequestParam(value = "recentLimit", defaultValue = "5") Integer recentLimit) {
        return ApiResult.success(dashboardService.getOverview(trendDays, topLimit, recentLimit));
    }
}
