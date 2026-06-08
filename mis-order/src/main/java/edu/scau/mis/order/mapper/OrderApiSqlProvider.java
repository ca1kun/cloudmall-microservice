package edu.scau.mis.order.mapper;


import edu.scau.mis.order.dto.OrderPageQueryDTO;

import java.util.Map;

public class OrderApiSqlProvider {
    public String selectMallPage(Map<String, Object> params) { return selectPageSql(params, true, false); }
    public String countMallPage(Map<String, Object> params) { return selectPageSql(params, true, true); }
    public String selectMerchantPage(Map<String, Object> params) { return selectPageSql(params, false, false); }
    public String countMerchantPage(Map<String, Object> params) { return selectPageSql(params, false, true); }

    private String selectPageSql(Map<String, Object> params, boolean mall, boolean count) {
        OrderPageQueryDTO q = (OrderPageQueryDTO) params.get("q");
        StringBuilder sql = new StringBuilder();
        if (count) {
            sql.append("SELECT COUNT(1) ");
        } else {
            sql.append("SELECT ");
            sql.append(" o.id AS orderId, o.order_sn AS orderNo, o.status AS statusCode, ");
            sql.append(" o.total_amount AS totalAmount, o.pay_amount AS payAmount, ");
            sql.append(" COALESCE((SELECT SUM(oi.product_quantity) FROM oms_order_item oi WHERE oi.order_id = o.id), 0) AS totalQuantity, ");
            sql.append(" o.create_time AS createTime, o.member_username AS buyerName, u.phone AS buyerPhone ");
        }
        sql.append(" FROM oms_order o LEFT JOIN sys_user u ON u.id = o.member_id WHERE 1 = 1 ");
        if (mall) sql.append(" AND o.member_id = #{memberId} ");
        if (q != null) {
            if (notBlank(q.getOrderNo())) sql.append(" AND o.order_sn LIKE CONCAT('%', #{q.orderNo}, '%') ");
            if (params.get("statusCode") != null) sql.append(" AND o.status = #{statusCode} ");
            if (notBlank(q.getStartTime())) sql.append(" AND o.create_time >= #{q.startTime} ");
            if (notBlank(q.getEndTime())) sql.append(" AND o.create_time <= #{q.endTime} ");
            if (!mall && notBlank(q.getBuyerName())) sql.append(" AND o.member_username LIKE CONCAT('%', #{q.buyerName}, '%') ");
            if (!mall && notBlank(q.getBuyerPhone())) sql.append(" AND u.phone LIKE CONCAT('%', #{q.buyerPhone}, '%') ");
        }
        if (!count) sql.append(" ORDER BY o.create_time DESC, o.id DESC LIMIT #{offset}, #{limit} ");
        return sql.toString();
    }

    private boolean notBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
