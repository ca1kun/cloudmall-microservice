package edu.scau.mis.order.mapper;

import edu.scau.mis.common.dto.PageResult;
import edu.scau.mis.order.dto.OrderPageQueryDTO;
import edu.scau.mis.order.dto.ReturnApplyDTO;
import edu.scau.mis.order.service.OrderApiService;
import edu.scau.mis.order.vo.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderApiMapper {

    @SelectProvider(type = OrderApiSqlProvider.class, method = "selectMallPage")
    List<OrderListVO> selectMallPage(@Param("memberId") Long memberId, @Param("q") OrderPageQueryDTO queryDTO,
                                     @Param("statusCode") Integer statusCode, @Param("offset") Integer offset, @Param("limit") Integer limit);

    @SelectProvider(type = OrderApiSqlProvider.class, method = "countMallPage")
    Long countMallPage(@Param("memberId") Long memberId, @Param("q") OrderPageQueryDTO queryDTO, @Param("statusCode") Integer statusCode);

    @SelectProvider(type = OrderApiSqlProvider.class, method = "selectMerchantPage")
    List<OrderListVO> selectMerchantPage(@Param("q") OrderPageQueryDTO queryDTO, @Param("statusCode") Integer statusCode,
                                         @Param("offset") Integer offset, @Param("limit") Integer limit);

    @SelectProvider(type = OrderApiSqlProvider.class, method = "countMerchantPage")
    Long countMerchantPage(@Param("q") OrderPageQueryDTO queryDTO, @Param("statusCode") Integer statusCode);

    @Select("""
            SELECT o.id AS orderId, o.order_sn AS orderNo, o.status AS statusCode,
                   o.total_amount AS totalAmount, o.pay_amount AS payAmount,
                   COALESCE((SELECT SUM(oi.product_quantity) FROM oms_order_item oi WHERE oi.order_id = o.id), 0) AS totalQuantity,
                   o.create_time AS createTime, o.member_username AS buyerName, u.phone AS buyerPhone,
                   o.receiver_name AS receiverName, o.receiver_phone AS receiverPhone, o.receiver_address AS receiverAddress,
                   o.refund_reason AS refundReason, o.refund_remark AS refundRemark,
                   o.refund_apply_time AS refundApplyTime, o.refund_audit_time AS refundAuditTime
            FROM oms_order o LEFT JOIN sys_user u ON u.id = o.member_id
            WHERE o.id = #{orderId} AND o.member_id = #{memberId}
            LIMIT 1
            """)
    OrderDetailVO selectMallDetail(@Param("memberId") Long memberId, @Param("orderId") Long orderId);

    @Select("""
            SELECT o.id AS orderId, o.order_sn AS orderNo, o.status AS statusCode,
                   o.total_amount AS totalAmount, o.pay_amount AS payAmount,
                   COALESCE((SELECT SUM(oi.product_quantity) FROM oms_order_item oi WHERE oi.order_id = o.id), 0) AS totalQuantity,
                   o.create_time AS createTime, o.member_username AS buyerName, u.phone AS buyerPhone,
                   o.receiver_name AS receiverName, o.receiver_phone AS receiverPhone, o.receiver_address AS receiverAddress,
                   o.refund_reason AS refundReason, o.refund_remark AS refundRemark,
                   o.refund_apply_time AS refundApplyTime, o.refund_audit_time AS refundAuditTime
            FROM oms_order o LEFT JOIN sys_user u ON u.id = o.member_id
            WHERE o.id = #{orderId}
            LIMIT 1
            """)
    OrderDetailVO selectMerchantDetail(@Param("orderId") Long orderId);

    @Select("""
            SELECT id AS itemId, product_id AS productId, product_name AS productName,
                   product_price AS price, product_quantity AS quantity,
                   IFNULL(product_price, 0) * IFNULL(product_quantity, 0) AS subtotal
            FROM oms_order_item
            WHERE order_id = #{orderId}
            ORDER BY id ASC
            """)
    List<OrderItemVO> selectItemsByOrderId(@Param("orderId") Long orderId);

    @Select("SELECT status FROM oms_order WHERE id = #{orderId} AND member_id = #{memberId} LIMIT 1")
    Integer selectMallOrderStatus(@Param("memberId") Long memberId, @Param("orderId") Long orderId);

    @Select("SELECT status FROM oms_order WHERE id = #{orderId} LIMIT 1")
    Integer selectMerchantOrderStatus(@Param("orderId") Long orderId);

    @Update("""
            UPDATE oms_order
            SET status = #{newStatus}, refund_reason = #{reason}, refund_remark = NULL,
                refund_apply_time = NOW(), refund_audit_time = NULL
            WHERE id = #{orderId} AND member_id = #{memberId}
            """)
    int applyReturn(@Param("memberId") Long memberId, @Param("orderId") Long orderId,
                    @Param("newStatus") Integer newStatus, @Param("reason") String reason);

    @Update("""
            UPDATE oms_order
            SET status = #{newStatus}, refund_remark = #{remark}, refund_audit_time = NOW()
            WHERE id = #{orderId}
            """)
    int auditReturn(@Param("orderId") Long orderId, @Param("newStatus") Integer newStatus, @Param("remark") String remark);

    @Select("SELECT status AS statusCode, COUNT(1) AS count FROM oms_order WHERE member_id = #{memberId} GROUP BY status")
    List<OrderStatusCountRow> selectMallStatusCount(@Param("memberId") Long memberId);

    @Select("SELECT status AS statusCode, COUNT(1) AS count FROM oms_order GROUP BY status")
    List<OrderStatusCountRow> selectMerchantStatusCount();
}
