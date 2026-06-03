package edu.scau.mis.product.service.impl;


import cn.hutool.json.JSONUtil;
import edu.scau.mis.common.dto.StockLockDTO;
import edu.scau.mis.common.utils.AliOssUtil;
import edu.scau.mis.common.domain.HttpCode;
import edu.scau.mis.common.exception.ServiceException;
import edu.scau.mis.common.domain.Product;
import edu.scau.mis.product.mapper.IProductMapper;
import edu.scau.mis.product.service.IProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ProductServiceImpl implements IProductService{
    @Autowired
    private IProductMapper productMapper;
    @Autowired
    private AliOssUtil aliOssUtil;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 商品详情缓存 key 前缀，完整 key 格式：product:detail:{productId}
     */
    private static final String PRODUCT_DETAIL_KEY_PREFIX = "product:detail:";

    /**
     * 商品列表缓存 key 前缀，完整 key 格式：product:list:{cacheKey}
     */
    private static final String PRODUCT_LIST_KEY_PREFIX = "product:list:";

    /**
     * 商品缓存默认过期时间，单位：分钟。
     */
    private static final long PRODUCT_CACHE_TTL = 30;

    @Override
    public Product getProductById(Long productId) {
        Product product = productMapper.selectProductById(productId);
        log.debug("查询商品成功：{}", product);
        return product;
    }

    @Override
    public Product getProductBySn(String productSn) {
        return productMapper.selectProductBySn(productSn);
    }
    //查询全部
    @Override
    public List<Product> getAllProducts() {
        return productMapper.selectAllProductList();
    }
    //根据名称、编码或类别查询
    @Override
    public List<Product> getProducts(Product product) {
        return productMapper.selectProductList(product);
    }

    @Override
    public int addProduct(Product product) {
        Product p = productMapper.selectProductBySn(product.getProductSn());
        if (p != null) {
            throw new ServiceException(HttpCode.PRODUCT_SN_ALREADY_EXIST);
        }
        product.setCreateTime(new Date());
        return productMapper.insertProduct(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 1. 开启事务，保证数据库操作的原子性
    public int updateProduct(Product product) {
        // 优化：如果有配置 MyMetaObjectHandler，这一行可以删掉，让 MP 自动填充
         product.setUpdateTime(new Date());

        // 2. 先查出数据库里的旧数据
        // 建议直接用 MP 原生方法，防止自定义 SQL 漏查字段
        Product oldProduct = productMapper.selectProductById(product.getProductId());

        if (oldProduct == null) {
            throw new ServiceException("商品不存在");
        }

        // 3. 执行数据库更新 (先动数据库！)
        // 注意：使用 updateById，MP 会自动根据 ID 更新非空字段
        int rows = productMapper.updateProduct(product);

        // 4. 如果数据库更新成功，再去处理图片删除
        if (rows > 0) {
            String oldImg = oldProduct.getImageUrl(); // 确保你实体类里叫 imageUrl 还是 image
            String newImg = product.getImageUrl();

            // 对比：如果旧图存在，且和新图不一样
            if (StringUtils.hasText(oldImg) && !oldImg.equals(newImg)) {
                try {
                    // 删除旧图 (放在最后做，且捕获异常，防止影响主流程)
                    aliOssUtil.delete(oldImg);
                } catch (Exception e) {
                    // 记录日志即可，不要抛出异常，否则会导致数据库回滚
                    // 图片删失败了就算了，留着当垃圾数据也比业务失败强
                    log.error("更新商品时删除旧图片失败: {}", oldImg, e);
                }
            }
        }

        return rows;
    }

    @Override
    public int deleteProduct(Long productId) {
        // 实际项目建议采用逻辑删除，这里为了演示直接物理删除
        return productMapper.deleteProductById(productId);
    }

    @Override
    public int deleteProductByIds(Long[] productIds) {
        return productMapper.deleteProductByIds(productIds);
    }

    @Override
    public List<Product> selectProducts(String productSn, String productName, Long productCategoryId) {
        return List.of();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void lockStock(List<StockLockDTO> list) {
        for (StockLockDTO dto : list) {
            System.out.println("正在扣减库存: 商品ID=" + dto.getProductId() + ", 扣减数量=" + dto.getCount());
            int rows = productMapper.lockStock(dto.getProductId(), dto.getCount());
            if (rows <= 0) {
                // 如果更新行数为0，说明库存不足
                throw new ServiceException("商品[" + dto.getProductId() + "]库存不足");
            }
        }
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void unlockStock(List<StockLockDTO> list) {
        for (StockLockDTO dto : list) {
            System.out.println("准备回滚库存: ID=" + dto.getProductId() + ", 数量=" + dto.getCount());
            productMapper.unlockStock(dto.getProductId(), dto.getCount());
        }
    }

    @Override
    public List<Product> listByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        return productMapper.selectBatchIds(ids);
    }

    // --- 商品缓存辅助方法 ---

    /**
     * 从 Redis 缓存中读取商品详情。
     *
     * @param productId 商品ID
     * @return 命中缓存时返回商品对象，未命中或参数为空时返回 null
     */
    @Override
    public Product loadProductFromCache(Long productId) {
        if (productId == null) {
            return null;
        }
        String productJson = stringRedisTemplate.opsForValue().get(buildProductDetailKey(productId));
        if (!StringUtils.hasText(productJson)) {
            return null;
        }
        return JSONUtil.toBean(productJson, Product.class);
    }

    /**
     * 将商品详情写入 Redis 缓存。
     *
     * @param product 需要缓存的商品对象，商品ID为空时不写入缓存
     */
    @Override
    public void cacheProduct(Product product) {
        if (product == null || product.getProductId() == null) {
            return;
        }
        stringRedisTemplate.opsForValue().set(
                buildProductDetailKey(product.getProductId()),
                JSONUtil.toJsonStr(product),
                PRODUCT_CACHE_TTL,
                TimeUnit.MINUTES
        );
    }

    /**
     * 删除指定商品详情缓存。
     *
     * @param productId 商品ID
     */
    @Override
    public void clearProductCache(Long productId) {
        if (productId == null) {
            return;
        }
        stringRedisTemplate.delete(buildProductDetailKey(productId));
    }

    /**
     * 从 Redis 缓存中读取商品列表。
     *
     * @param cacheKey 列表缓存业务 key，例如 all、category:1、search:xxx
     * @return 命中缓存时返回商品列表，未命中或参数为空时返回 null
     */
    @Override
    public List<Product> loadProductListFromCache(String cacheKey) {
        if (!StringUtils.hasText(cacheKey)) {
            return null;
        }
        String productListJson = stringRedisTemplate.opsForValue().get(buildProductListKey(cacheKey));
        if (!StringUtils.hasText(productListJson)) {
            return null;
        }
        return JSONUtil.toList(JSONUtil.parseArray(productListJson), Product.class);
    }

    /**
     * 将商品列表写入 Redis 缓存。
     *
     * @param cacheKey 列表缓存业务 key，例如 all、category:1、search:xxx
     * @param products 需要缓存的商品列表
     */
    @Override
    public void cacheProductList(String cacheKey, List<Product> products) {
        if (!StringUtils.hasText(cacheKey) || products == null) {
            return;
        }
        stringRedisTemplate.opsForValue().set(
                buildProductListKey(cacheKey),
                JSONUtil.toJsonStr(products),
                PRODUCT_CACHE_TTL,
                TimeUnit.MINUTES
        );
    }

    /**
     * 删除指定商品列表缓存。
     *
     * @param cacheKey 列表缓存业务 key
     */
    @Override
    public void clearProductListCache(String cacheKey) {
        if (!StringUtils.hasText(cacheKey)) {
            return;
        }
        stringRedisTemplate.delete(buildProductListKey(cacheKey));
    }

    /**
     * 删除全部商品列表缓存。
     *
     * 备注：用于新增、修改、删除商品后清理列表类缓存，避免旧列表继续返回。
     */
    @Override
    public void clearAllProductListCache() {
        Set<String> keys = stringRedisTemplate.keys(PRODUCT_LIST_KEY_PREFIX + "*");
        if (keys == null || keys.isEmpty()) {
            return;
        }
        stringRedisTemplate.delete(keys);
    }

    /**
     * 构建商品详情缓存 key。
     *
     * @param productId 商品ID
     * @return Redis key
     */
    @Override
    public String buildProductDetailKey(Long productId) {
        return PRODUCT_DETAIL_KEY_PREFIX + productId;
    }

    /**
     * 构建商品列表缓存 key。
     *
     * @param cacheKey 列表缓存业务 key
     * @return Redis key
     */
    @Override
    public String buildProductListKey(String cacheKey) {
        return PRODUCT_LIST_KEY_PREFIX + cacheKey;
    }

}
