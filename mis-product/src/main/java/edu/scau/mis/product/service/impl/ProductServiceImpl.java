package edu.scau.mis.product.service.impl;


import edu.scau.mis.common.dto.StockLockDTO;
import edu.scau.mis.common.utils.AliOssUtil;
import edu.scau.mis.common.domain.HttpCode;
import edu.scau.mis.common.exception.ServiceException;
import edu.scau.mis.common.domain.Product;
import edu.scau.mis.product.mapper.IProductMapper;
import edu.scau.mis.product.service.IProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ProductServiceImpl implements IProductService{
    @Autowired
    private IProductMapper productMapper;
    @Autowired
    private AliOssUtil aliOssUtil;
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

}
