package edu.scau.mis.product.service.impl;

import edu.scau.mis.common.vo.dashboard.CategoryShareVo;
import edu.scau.mis.common.vo.dashboard.ChannelOrdersVo;
import edu.scau.mis.common.vo.dashboard.DashboardOverviewVo;
import edu.scau.mis.common.vo.dashboard.MetricCardVo;
import edu.scau.mis.common.vo.dashboard.NameValueItemVo;
import edu.scau.mis.common.vo.dashboard.RecentOrderItemVo;
import edu.scau.mis.common.vo.dashboard.RecentOrdersVo;
import edu.scau.mis.common.vo.dashboard.TopProductItemVo;
import edu.scau.mis.common.vo.dashboard.TopProductsVo;
import edu.scau.mis.common.vo.dashboard.TrendDataVo;
import edu.scau.mis.common.vo.dashboard.TrendPointVo;
import edu.scau.mis.product.mapper.IDashboardMapper;
import edu.scau.mis.product.service.IDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Codex added: 商家大屏服务实现，基于现有销售、商品、分类表聚合数据。
 */
@Service
public class DashboardServiceImpl implements IDashboardService {

    private static final ZoneId ZONE_ID = ZoneId.systemDefault();
    private static final DateTimeFormatter DATE_KEY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_LABEL_FORMATTER = DateTimeFormatter.ofPattern("MM-dd");
    private static final DecimalFormat INTEGER_FORMATTER = new DecimalFormat("#,##0");

    @Autowired
    private IDashboardMapper dashboardMapper;

    /**
     * Codex added: 核心指标来自订单、商品、近 24 小时活跃创建人等真实表数据。
     */
    @Override
    public Map<String, MetricCardVo> getMetrics() {
        LocalDateTime now = LocalDateTime.now();
        Date todayStart = toDate(now.toLocalDate().atStartOfDay());
        Date tomorrowStart = toDate(now.toLocalDate().plusDays(1).atStartOfDay());
        Date yesterdayStart = toDate(now.toLocalDate().minusDays(1).atStartOfDay());
        Date last24Hours = toDate(now.minusHours(24));

        BigDecimal totalSales = nvl(dashboardMapper.selectTotalSales());
        BigDecimal todaySales = nvl(dashboardMapper.selectSalesBetween(todayStart, tomorrowStart));
        BigDecimal yesterdaySales = nvl(dashboardMapper.selectSalesBetween(yesterdayStart, todayStart));
        int todayOrders = nvl(dashboardMapper.countOrdersBetween(todayStart, tomorrowStart));
        int yesterdayOrders = nvl(dashboardMapper.countOrdersBetween(yesterdayStart, todayStart));
        int activeUsers = nvl(dashboardMapper.countActiveUsersSince(last24Hours));
        int goodsCount = nvl(dashboardMapper.countAvailableProducts());

        Map<String, MetricCardVo> data = new LinkedHashMap<>();
        data.put("sales", buildMetric("总销售额", totalSales, formatMoney(totalSales), trend(todaySales, yesterdaySales), "较昨日销售额变化"));
        data.put("orders", buildMetric("今日订单", todayOrders, INTEGER_FORMATTER.format(todayOrders), trend(todayOrders, yesterdayOrders), "今日实时成交 " + todayOrders + " 单"));
        data.put("activeUsers", buildMetric("活跃用户", activeUsers, INTEGER_FORMATTER.format(activeUsers), 0D, "近 24h 下单用户/订单创建人"));
        data.put("goodsCount", buildMetric("在售商品", goodsCount, INTEGER_FORMATTER.format(goodsCount), 0D, "按库存大于 0 统计"));
        return data;
    }

    /**
     * Codex added: 趋势图按天从 pos_sale 聚合，并补齐无订单日期。
     */
    @Override
    public TrendDataVo getTrend(Integer days) {
        int safeDays = clamp(days, 3, 30);
        LocalDate endDate = LocalDate.now().plusDays(1);
        LocalDate startDate = endDate.minusDays(safeDays);
        List<TrendPointVo> points = dashboardMapper.selectTrend(toDate(startDate.atStartOfDay()), toDate(endDate.atStartOfDay()));
        Map<String, TrendPointVo> pointMap = points.stream()
                .collect(Collectors.toMap(TrendPointVo::getDateLabel, Function.identity(), (a, b) -> a));

        List<String> labels = new ArrayList<>();
        List<BigDecimal> sales = new ArrayList<>();
        List<Integer> orders = new ArrayList<>();
        List<BigDecimal> averageOrderValue = new ArrayList<>();

        for (int i = 0; i < safeDays; i++) {
            LocalDate day = startDate.plusDays(i);
            TrendPointVo point = pointMap.get(day.format(DATE_KEY_FORMATTER));
            BigDecimal daySales = point == null ? BigDecimal.ZERO : nvl(point.getSales());
            int dayOrders = point == null ? 0 : nvl(point.getOrders());
            labels.add(day.format(DATE_LABEL_FORMATTER));
            sales.add(daySales);
            orders.add(dayOrders);
            averageOrderValue.add(dayOrders == 0 ? BigDecimal.ZERO : daySales.divide(BigDecimal.valueOf(dayOrders), 2, RoundingMode.HALF_UP));
        }
        return new TrendDataVo(labels, sales, orders, averageOrderValue);
    }

    /**
     * Codex added: 品类占比基于订单明细销量聚合，再换算为百分比。
     */
    @Override
    public CategoryShareVo getCategoryShare() {
        List<NameValueItemVo> rows = dashboardMapper.selectCategorySalesShare();
        int total = rows.stream().mapToInt(item -> nvl(item.getValue())).sum();
        if (total <= 0) {
            return new CategoryShareVo(new ArrayList<>());
        }
        List<NameValueItemVo> data = rows.stream()
                .map(item -> new NameValueItemVo(item.getName(), BigDecimal.valueOf(nvl(item.getValue()) * 100L)
                        .divide(BigDecimal.valueOf(total), 0, RoundingMode.HALF_UP)
                        .intValue()))
                .collect(Collectors.toList());
        return new CategoryShareVo(data);
    }

    /**
     * Codex added: 当前项目没有渠道字段，按现有 POS 销售实体返回线下 POS 渠道真实订单量。
     */
    @Override
    public ChannelOrdersVo getChannelOrders(String period) {
        Date[] range = periodRange(period);
        int orders = nvl(dashboardMapper.countPosChannelOrders(range[0], range[1]));
        return new ChannelOrdersVo(Arrays.asList("\u5546\u57ce\u8ba2\u5355"), Arrays.asList(orders));
    }

    /**
     * Codex added: 热销商品排行来自 pos_sale_item 销量聚合。
     */
    @Override
    public TopProductsVo getTopProducts(Integer limit, String period) {
        int safeLimit = clamp(limit, 5, 20);
        Date[] range = periodRange(period);
        List<TopProductItemVo> list = dashboardMapper.selectTopProducts(range[0], range[1], safeLimit);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setRank(i + 1);
        }
        return new TopProductsVo(list);
    }

    /**
     * Codex added: 最新订单来自 pos_sale，状态文本按当前 SaleStatusEnum 小写 code 映射给前端。
     */
    @Override
    public RecentOrdersVo getRecentOrders(Integer limit) {
        int safeLimit = clamp(limit, 5, 20);
        List<RecentOrderItemVo> list = dashboardMapper.selectRecentOrders(safeLimit);
        for (RecentOrderItemVo item : list) {
            BigDecimal amount = nvl(item.getAmount());
            item.setAmount(amount);
            item.setAmountText(formatMoney(amount));
            item.setStatus(normalizeStatus(item.getStatus()));
            item.setStatusText(statusText(item.getStatus()));
            item.setStatusType(statusType(item.getStatus()));
        }
        return new RecentOrdersVo(list);
    }

    /**
     * Codex added: 聚合接口复用各单项查询，保持响应结构与文档一致。
     */
    @Override
    public DashboardOverviewVo getOverview(Integer trendDays, Integer topLimit, Integer recentLimit) {
        return new DashboardOverviewVo(
                getMetrics(),
                getTrend(trendDays),
                getCategoryShare(),
                getChannelOrders("week"),
                getTopProducts(topLimit, "week"),
                getRecentOrders(recentLimit)
        );
    }

    private MetricCardVo buildMetric(String label, Number value, String valueText, Double trend, String subtext) {
        String direction = trend >= 0 ? "up" : "down";
        String trendText = (trend >= 0 ? "+" : "") + BigDecimal.valueOf(trend).setScale(1, RoundingMode.HALF_UP) + "%";
        return new MetricCardVo(label, value, valueText, trend, trendText, direction, subtext);
    }

    private Double trend(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            return current == null || current.compareTo(BigDecimal.ZERO) == 0 ? 0D : 100D;
        }
        return current.subtract(previous)
                .multiply(BigDecimal.valueOf(100))
                .divide(previous, 1, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private Double trend(Integer current, Integer previous) {
        return trend(BigDecimal.valueOf(nvl(current)), BigDecimal.valueOf(nvl(previous)));
    }

    private Date[] periodRange(String period) {
        LocalDate today = LocalDate.now();
        LocalDate start = "month".equalsIgnoreCase(period)
                ? today.withDayOfMonth(1)
                : "all".equalsIgnoreCase(period)
                ? LocalDate.of(1970, 1, 1)
                : today.minusDays(6);
        LocalDate end = today.plusDays(1);
        return new Date[]{toDate(start.atStartOfDay()), toDate(end.atStartOfDay())};
    }

    private int clamp(Integer value, int min, int max) {
        if (value == null) {
            return min;
        }
        return Math.max(min, Math.min(max, value));
    }

    private BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private int nvl(Integer value) {
        return value == null ? 0 : value;
    }

    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZONE_ID).toInstant());
    }

    private String formatMoney(BigDecimal value) {
        return "¥ " + INTEGER_FORMATTER.format(nvl(value).setScale(0, RoundingMode.HALF_UP));
    }

    private String normalizeStatus(String status) {
        if (status == null) {
            return "UNKNOWN";
        }
        return switch (status.trim()) {
            case "0" -> "PENDING_PAYMENT";
            case "1" -> "PAID";
            case "2" -> "SHIPPED";
            case "3" -> "COMPLETED";
            case "4" -> "CANCELED";
            case "5" -> "REFUNDING";
            case "6" -> "REFUNDED";
            case "7" -> "RETURN_REJECTED";
            default -> status.toUpperCase();
        };
    }

    private String statusText(String status) {
        return switch (status) {
            case "PENDING_PAYMENT" -> "\u5f85\u4ed8\u6b3e";
            case "PAID" -> "\u5df2\u652f\u4ed8";
            case "RESERVED" -> "\u5df2\u9884\u8ba2";
            case "DELIVERED", "SHIPPED" -> "\u5df2\u53d1\u8d27";
            case "COMPLETED" -> "\u5df2\u5b8c\u6210";
            case "UNPAID" -> "\u672a\u652f\u4ed8";
            case "CANCELLED", "CANCELED" -> "\u5df2\u53d6\u6d88";
            case "REFUNDING" -> "\u9000\u6b3e\u4e2d";
            case "REFUNDED" -> "\u5df2\u9000\u6b3e";
            case "RETURN_REJECTED" -> "\u9000\u8d27\u88ab\u62d2";
            default -> "\u672a\u77e5";
        };
    }
    private String statusType(String status) {
        return switch (status) {
            case "PAID", "COMPLETED" -> "success";
            case "RESERVED", "UNPAID", "PENDING_PAYMENT", "REFUNDING" -> "warning";
            case "DELIVERED", "SHIPPED", "REFUNDED", "RETURN_REJECTED" -> "info";
            case "CANCELLED", "CANCELED" -> "danger";
            default -> "info";
        };
    }
}
