// 文件路径: D:/idea/workspace/demo/isdp/isdp-boot3Plus/mis-pos/src/main/java/edu/scau/mis/pos/domain/SaleItem.java
package edu.scau.mis.product.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import edu.scau.mis.common.domain.BaseEntity;
import edu.scau.mis.common.domain.Product;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class SaleItem extends BaseEntity implements Serializable {
    private Long saleItemId;
    // private Product product; // 【移除】不再持有完整的Product对象
    private String saleNo;
    // 【新增】直接存储商品快照信息
    private String productSn;
    private String productName;

    private int quantity;
    @JsonSerialize(using = ToStringSerializer.class)
    private Long saleId;
    private Long productId; // 【保留】这是与Product关联的核心ID
    private BigDecimal price; // 商品价格快照
    private String status;
    private String delFlag;

    /**
     * 【新增】一个便利的构造器，用于根据Product创建SaleItem
     * @param product 商品领域对象
     * @param quantity 数量
     */
    public SaleItem(Product product, int quantity) {
        if (product != null) {
            this.productId = product.getProductId();
            this.productSn = product.getProductSn();
            this.productName = product.getProductName();
            this.price = product.getPrice(); // 创建价格快照
        }
        this.quantity = quantity;
    }

    // 【新增】为JSON反序列化保留一个无参构造器
    public SaleItem() {}
}
