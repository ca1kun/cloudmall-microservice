package edu.scau.mis.product.mapper;

import edu.scau.mis.product.vo.dashboard.NameValueItemVo;
import edu.scau.mis.product.vo.dashboard.RecentOrderItemVo;
import edu.scau.mis.product.vo.dashboard.TopProductItemVo;
import edu.scau.mis.product.vo.dashboard.TrendPointVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Codex added: 商家大屏数据库聚合查询 Mapper。
 */
public interface IDashboardMapper {

    /**
     * Codex added: 查询已成交订单总销售额。
     */
    BigDecimal selectTotalSales();

    /**
     * Codex added: 查询指定时间范围内的销售额。
     */
    BigDecimal selectSalesBetween(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * Codex added: 查询指定时间范围内的订单数。
     */
    Integer countOrdersBetween(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * Codex added: 查询指定时间之后的活跃用户数；当前项目没有独立访客表，使用订单 create_by 去重统计。
     */
    Integer countActiveUsersSince(@Param("startTime") Date startTime);

    /**
     * Codex added: 查询在售商品数；当前项目用 stock > 0 判断在售。
     */
    Integer countAvailableProducts();

    /**
     * Codex added: 查询按天聚合的销售趋势。
     */
    List<TrendPointVo> selectTrend(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * Codex added: 查询品类销量占比原始销量。
     */
    List<NameValueItemVo> selectCategorySalesShare();

    /**
     * Codex added: 查询指定时间范围内 POS 渠道订单数。
     */
    Integer countPosChannelOrders(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * Codex added: 查询热销商品排行。
     */
    List<TopProductItemVo> selectTopProducts(@Param("startTime") Date startTime,
                                             @Param("endTime") Date endTime,
                                             @Param("limit") Integer limit);

    /**
     * Codex added: 查询最新订单。
     */
    List<RecentOrderItemVo> selectRecentOrders(@Param("limit") Integer limit);
}
