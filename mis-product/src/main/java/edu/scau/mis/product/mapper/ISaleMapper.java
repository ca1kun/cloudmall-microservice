package edu.scau.mis.product.mapper;

import edu.scau.mis.common.annotation.AutoFill;
import edu.scau.mis.product.domain.Sale;

import java.util.List;


public interface ISaleMapper {
    /**
     *根据id查询订单对象
     * @param saleId
     * @return
     */
     Sale selectSaleById(Long saleId);

    /**
     *查询订单列表
     * @param sale
     * @return
     */
    List<Sale> selectSaleList(Sale sale);

    /**
     *新增订单对象
     * @param sale
     * @return
     */
    @AutoFill(isInsert = true)
     int insertSale(Sale sale);

    /**
     *更新订单对象
     * @param sale
     * @return
     */
    @AutoFill(isInsert = false)
     int updateSale(Sale sale);
}
