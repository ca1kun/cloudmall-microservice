// Product Service接口
package edu.scau.mis.product.service;

import edu.scau.mis.common.domain.Product;
import edu.scau.mis.common.dto.StockLockDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IProductService{
    Product getProductById(Long productId);
    List<Product> getAllProducts();
    Product getProductBySn(String productSn);
    List<Product> getProducts(Product product);
    int addProduct(Product product);
    int updateProduct(Product product);
    int deleteProduct(Long productId);
    int deleteProductByIds(Long[] productIds);

    List<Product> selectProducts(String productSn, String productName, Long productCategoryId);

    @Transactional(rollbackFor = Exception.class)
    void lockStock(List<StockLockDTO> list);

    @Transactional(rollbackFor = Exception.class)
    void unlockStock(List<StockLockDTO> list);
    // 在 IProductService 接口中添加以下方法
    List<Product> listByIds(List<Long> ids);


}