package edu.scau.mis.api.controller;

import edu.scau.mis.common.domain.ApiResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Codex added: merchant dashboard API controller.
 * These endpoints follow dashboard-api.md and return frontend-friendly mock data first.
 */
@RestController
@RequestMapping("/merchant/dashboard")
@Tag(name = "商家大屏")
public class MerchantDashboardController {

    /**
     * Codex added: core dashboard metrics endpoint.
     */
    @GetMapping("/metrics")
    @Operation(summary = "获取商家大屏核心指标")
    public ApiResult<Map<String, MetricCard>> metrics() {
        Map<String, MetricCard> data = new LinkedHashMap<>();
        data.put("sales", new MetricCard("总销售额", 1286560, "¥ 1,286,560", 18.4, "+18.4%", "up", "较上周提升 18.4%"));
        data.put("orders", new MetricCard("今日订单", 358, "358", 12.8, "+12.8%", "up", "今日实时成交 358 单"));
        data.put("activeUsers", new MetricCard("活跃用户", 8920, "8,920", 6.2, "+6.2%", "up", "近 24h 访问持续增长"));
        data.put("goodsCount", new MetricCard("在售商品", 1284, "1,284", -1.3, "-1.3%", "down", "库存结构保持稳定"));
        return ApiResult.success(data);
    }

    /**
     * Codex added: sales and order trend endpoint.
     */
    @GetMapping("/trend")
    @Operation(summary = "获取销售与订单趋势")
    public ApiResult<TrendData> trend(
            @Parameter(description = "查询天数，范围 3~30，默认 7")
            @RequestParam(value = "days", defaultValue = "7") Integer days) {
        int safeDays = clamp(days, 3, 30);
        List<String> labels = buildDayLabels(safeDays);
        List<Integer> sales = buildSeries(safeDays, 98, 17);
        List<Integer> orders = buildSeries(safeDays, 62, 8);
        List<Integer> averageOrderValue = buildSeries(safeDays, 1590, 59);
        return ApiResult.success(new TrendData(labels, sales, orders, averageOrderValue));
    }

    /**
     * Codex added: category share endpoint.
     */
    @GetMapping("/category-share")
    @Operation(summary = "获取品类占比")
    public ApiResult<CategoryShareData> categoryShare() {
        List<NameValueItem> data = Arrays.asList(
                new NameValueItem("手机数码", 38),
                new NameValueItem("电脑办公", 22),
                new NameValueItem("日用百货", 18),
                new NameValueItem("食品饮料", 14),
                new NameValueItem("其他", 8)
        );
        return ApiResult.success(new CategoryShareData(data));
    }

    /**
     * Codex added: channel order comparison endpoint.
     */
    @GetMapping("/channel-orders")
    @Operation(summary = "获取渠道订单数据")
    public ApiResult<ChannelOrdersData> channelOrders(
            @Parameter(description = "统计周期：week 本周 / month 本月")
            @RequestParam(value = "period", defaultValue = "week") String period) {
        boolean month = "month".equalsIgnoreCase(period);
        List<String> channels = Arrays.asList("小程序", "APP", "PC端", "门店", "抖音");
        List<Integer> orders = month
                ? Arrays.asList(1320, 1168, 914, 780, 632)
                : Arrays.asList(320, 268, 214, 180, 132);
        return ApiResult.success(new ChannelOrdersData(channels, orders));
    }

    /**
     * Codex added: top products endpoint.
     */
    @GetMapping("/top-products")
    @Operation(summary = "获取热销商品排行")
    public ApiResult<TopProductsData> topProducts(
            @Parameter(description = "返回条数，范围 5~20，默认 10")
            @RequestParam(value = "limit", defaultValue = "10") Integer limit,
            @Parameter(description = "统计周期：week 本周 / month 本月 / all 全部")
            @RequestParam(value = "period", defaultValue = "week") String period) {
        int safeLimit = clamp(limit, 5, 20);
        List<String> names = Arrays.asList(
                "小米 14 Pro", "iPhone 15 Pro Max", "华为 Mate 60 Pro", "罗技 MX Master 3S", "三只松鼠坚果礼盒",
                "维达抽纸 4 层", "美的空气炸锅", "戴尔 27 寸显示器", "蕉下防晒伞", "农夫山泉 24 瓶"
        );
        int multiplier = "month".equalsIgnoreCase(period) ? 4 : "all".equalsIgnoreCase(period) ? 12 : 1;
        List<TopProductItem> list = new ArrayList<>();
        for (int i = 0; i < safeLimit; i++) {
            String name = names.get(i % names.size());
            list.add(new TopProductItem(i + 1, name, (1260 - i * 83) * multiplier));
        }
        return ApiResult.success(new TopProductsData(list));
    }

    /**
     * Codex added: recent orders endpoint.
     */
    @GetMapping("/recent-orders")
    @Operation(summary = "获取最新订单")
    public ApiResult<RecentOrdersData> recentOrders(
            @Parameter(description = "返回条数，范围 5~20，默认 5")
            @RequestParam(value = "limit", defaultValue = "5") Integer limit) {
        int safeLimit = clamp(limit, 5, 20);
        List<RecentOrderItem> list = new ArrayList<>();
        String[] statuses = {"PAID", "PENDING_SHIPMENT", "SHIPPED", "COMPLETED", "CANCELED"};
        for (int i = 0; i < safeLimit; i++) {
            String status = statuses[i % statuses.length];
            BigDecimal amount = BigDecimal.valueOf(1299 + i * 320L);
            list.add(new RecentOrderItem(
                    String.format("OD20260426%04d", i + 1),
                    i % 2 == 0 ? "张先生" : "李女士",
                    amount,
                    "¥ " + amount.toPlainString(),
                    status,
                    statusText(status),
                    statusType(status),
                    String.format("2026-04-26 10:%02d:00", 30 - i)
            ));
        }
        return ApiResult.success(new RecentOrdersData(list));
    }

    /**
     * Codex added: aggregate dashboard overview endpoint.
     */
    @GetMapping("/overview")
    @Operation(summary = "获取商家大屏聚合数据")
    public ApiResult<DashboardOverviewData> overview(
            @RequestParam(value = "trendDays", defaultValue = "7") Integer trendDays,
            @RequestParam(value = "topLimit", defaultValue = "10") Integer topLimit,
            @RequestParam(value = "recentLimit", defaultValue = "5") Integer recentLimit) {
        DashboardOverviewData data = new DashboardOverviewData(
                metrics().getData(),
                trend(trendDays).getData(),
                categoryShare().getData(),
                channelOrders("week").getData(),
                topProducts(topLimit, "week").getData(),
                recentOrders(recentLimit).getData()
        );
        return ApiResult.success(data);
    }

    private int clamp(Integer value, int min, int max) {
        if (value == null) {
            return min;
        }
        return Math.max(min, Math.min(max, value));
    }

    private List<String> buildDayLabels(int days) {
        String[] weekLabels = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        List<String> labels = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            labels.add(days == 7 ? weekLabels[i] : "第" + (i + 1) + "天");
        }
        return labels;
    }

    private List<Integer> buildSeries(int days, int base, int step) {
        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            values.add(base + i * step + (i % 3) * 7);
        }
        return values;
    }

    private String statusText(String status) {
        return switch (status) {
            case "PAID" -> "已支付";
            case "PENDING_SHIPMENT" -> "待发货";
            case "SHIPPED" -> "配送中";
            case "COMPLETED" -> "已完成";
            case "CANCELED" -> "已取消";
            default -> "未知";
        };
    }

    private String statusType(String status) {
        return switch (status) {
            case "PAID", "COMPLETED" -> "success";
            case "PENDING_SHIPMENT" -> "warning";
            case "SHIPPED" -> "info";
            case "CANCELED" -> "danger";
            default -> "info";
        };
    }

    /**
     * Codex added: metric card response item.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "商家大屏指标卡片")
    public static class MetricCard {
        private String label;
        private Number value;
        private String valueText;
        private Double trend;
        private String trendText;
        private String direction;
        private String subtext;
    }

    /**
     * Codex added: trend response data.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "销售与订单趋势")
    public static class TrendData {
        private List<String> days;
        private List<Integer> sales;
        private List<Integer> orders;
        private List<Integer> averageOrderValue;
    }

    /**
     * Codex added: category share wrapper.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "品类占比")
    public static class CategoryShareData {
        private List<NameValueItem> data;
    }

    /**
     * Codex added: name-value item.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "名称数值项")
    public static class NameValueItem {
        private String name;
        private Integer value;
    }

    /**
     * Codex added: channel orders response data.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "渠道订单数据")
    public static class ChannelOrdersData {
        private List<String> channels;
        private List<Integer> orders;
    }

    /**
     * Codex added: top products wrapper.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "热销商品排行")
    public static class TopProductsData {
        private List<TopProductItem> list;
    }

    /**
     * Codex added: top product item.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "热销商品排行项")
    public static class TopProductItem {
        private Integer rank;
        private String name;
        private Integer count;
    }

    /**
     * Codex added: recent orders wrapper.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "最新订单")
    public static class RecentOrdersData {
        private List<RecentOrderItem> list;
    }

    /**
     * Codex added: recent order item.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "最新订单项")
    public static class RecentOrderItem {
        private String orderNo;
        private String customer;
        private BigDecimal amount;
        private String amountText;
        private String status;
        private String statusText;
        private String statusType;
        private String createTime;
    }

    /**
     * Codex added: overview response data.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "商家大屏聚合数据")
    public static class DashboardOverviewData {
        private Map<String, MetricCard> metrics;
        private TrendData trend;
        private CategoryShareData categoryShare;
        private ChannelOrdersData channelOrders;
        private TopProductsData topProducts;
        private RecentOrdersData recentOrders;
    }
}
