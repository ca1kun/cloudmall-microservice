package edu.scau.mis.product.mapper;

import edu.scau.mis.common.annotation.AutoFill;
import edu.scau.mis.product.domain.SaleItem;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface ISaleItemMapper {
    /**
     *根据id查询订单明细对象
     * @param saleItemId
     * @return
     */
     SaleItem selectSaleItemById(Long saleItemId);

    /**
     *查询订单明细列表
     * @param saleItem
     * @return
     */
     List<SaleItem> selectSaleItemList(SaleItem saleItem);

    /**
     *新增订单明细对象
     * @param saleItem
     * @return
     */
    @AutoFill(isInsert = true)
     int insertSaleItem(SaleItem saleItem);

    /**
     * 更新订单对象
     * @param saleItem
     * @return
     */
    @AutoFill(isInsert = false)
     int updateSaleItem(SaleItem saleItem);

    /**
     *批量新增订单明细
     * @param saleItemList
     * @return
     */
    @AutoFill(isInsert=true)
     int batchInsertSaleItemOfCurrentSale(List<SaleItem> saleItemList);


    List<SaleItem> selectSaleItemsBySaleId(Long saleId);

    /**
     * 根据 Sale ID 批量更新订单明细的状态和更新时间
     * @param saleId 订单ID
     * @param status 新的状态
     * @param updateTime 新的更新时间
     * @return 受影响的行数
     */
    int updateStatusBySaleId(@Param("saleId") Long saleId,
                             @Param("status") String status,
                             @Param("updateBy") String updateBy,
                             @Param("updateTime") Date updateTime);
}
