package edu.scau.mis.product.mapper;

import edu.scau.mis.common.annotation.AutoFill;
import edu.scau.mis.common.domain.Product;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Primary
public interface IProductMapper {


    Product selectProductById(Long productId);
    Product selectProductBySn(String productSn);
    //查询全部
    List<Product> selectAllProductList();
    //根据编码、名称、类别查询
    List<Product> selectProductList(Product product);
    @AutoFill
    int insertProduct(Product product);
    @AutoFill(isInsert = false)
    int updateProduct(Product product);
    int deleteProductById(Long productId);

    int deleteProductByIds(Long[] longs);
    List<Product> selectProductByNameAndCategoryId(String productName, Long productCategoryId);

    // 关键 SQL：stock - #{count}，并且条件是 stock >= #{count}
    @Update("UPDATE pos_product SET stock = stock - #{count} WHERE product_id = #{id} AND stock >= #{count}")
    int lockStock(@Param("id") Long id, @Param("count") Integer count);
    // 解锁库存（加回去）
    @Update("UPDATE pos_product SET stock = stock + #{count} WHERE product_id = #{id}")
    void unlockStock(@Param("id") Long id, @Param("count") Integer count);

    // 在 ProductMapper 接口中添加
    List<Product> selectBatchIds(@Param("ids") List<Long> ids);

}
