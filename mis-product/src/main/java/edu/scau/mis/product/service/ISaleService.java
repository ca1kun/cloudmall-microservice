package edu.scau.mis.product.service;


import com.baomidou.mybatisplus.extension.service.IService;
import edu.scau.mis.product.domain.Payment;
import edu.scau.mis.product.domain.Sale;
import edu.scau.mis.product.domain.SaleItem;
import edu.scau.mis.common.domain.Product;
import edu.scau.mis.common.vo.PaymentResultVo;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public interface ISaleService extends IService<Payment> {
    /**
     * 创建一个新的销售单
     * @return 新创建的销售单
     */
    Sale makeNewSale();

    /**
     * 向当前销售单添加商品项
     * @param saleId 销售单ID
     * @param product 商品
     * @param quantity 数量
     * @return 更新后的销售单商品列表
     */
    List<SaleItem> makeLineItem(Long saleId, Product product, int quantity);

    /**
     * 结束当前销售单，计算总价
     * @param saleId 销售单ID
     * @return 结束后的销售单
     */
    Sale endSale(Long saleId);

    /**
     * 支付当前销售单
     * @param saleId 销售单ID
     * @param cashTendered 顾客支付金额
     * @param payMethod 支付方式
     * @return 支付结果，包含找零
     */
    @Transactional
    PaymentResultVo makePayment(Long saleId, BigDecimal cashTendered, String payMethod);

    /**
     * 改变销售单中某个商品的数量
     * @param saleId 销售单ID
     * @param productId 商品ID
     * @param quantity 新的数量
     * @return 更新后的销售单商品列表
     */
    List<SaleItem> changeItemQuantity(Long saleId, Long productId, int quantity);

    /**
     * 从销售单中删除一个商品项
     * @param saleId 销售单ID
     * @param productId 商品ID
     * @return 更新后的销售单商品列表
     */
    List<SaleItem> deleteSaleItem(Long saleId, Long productId);

    /**
     * 挂起当前订单
     * @param saleId 销售单ID
     * @return 被挂起的销售单
     */
    @Transactional
    Sale holdCurrentOrder(Long saleId);

    List<SaleItem> listSaleItemBySaleId(Long saleId);

    List<Sale> selectSaleList(Sale sale);

    @Transactional
    void insertPayment(Payment payment);
}
