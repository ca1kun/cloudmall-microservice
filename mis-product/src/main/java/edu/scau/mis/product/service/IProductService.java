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

    /**
     * 从 Redis 缓存中读取商品详情。
     *
     * @param productId 商品ID
     * @return 命中缓存时返回商品对象，未命中或参数为空时返回 null
     */
    Product loadProductFromCache(Long productId);

    /**
     * 将商品详情写入 Redis 缓存。
     *
     * @param product 需要缓存的商品对象，商品ID为空时不写入缓存
     */
    void cacheProduct(Product product);

    /**
     * 删除指定商品详情缓存。
     *
     * @param productId 商品ID
     */
    void clearProductCache(Long productId);

    /**
     * 从 Redis 缓存中读取商品列表。
     *
     * @param cacheKey 列表缓存业务 key，例如 all、category:1、search:xxx
     * @return 命中缓存时返回商品列表，未命中或参数为空时返回 null
     */
    List<Product> loadProductListFromCache(String cacheKey);

    /**
     * 将商品列表写入 Redis 缓存。
     *
     * @param cacheKey 列表缓存业务 key，例如 all、category:1、search:xxx
     * @param products 需要缓存的商品列表
     */
    void cacheProductList(String cacheKey, List<Product> products);

    /**
     * 删除指定商品列表缓存。
     *
     * @param cacheKey 列表缓存业务 key
     */
    void clearProductListCache(String cacheKey);

    /**
     * 删除全部商品列表缓存。
     *
     * 备注：用于新增、修改、删除商品后清理列表类缓存，避免旧列表继续返回。
     */
    void clearAllProductListCache();

    /**
     * 构建商品详情缓存 key。
     *
     * @param productId 商品ID
     * @return Redis key
     */
    String buildProductDetailKey(Long productId);

    /**
     * 构建商品列表缓存 key。
     *
     * @param cacheKey 列表缓存业务 key
     * @return Redis key
     */
    String buildProductListKey(String cacheKey);


}
