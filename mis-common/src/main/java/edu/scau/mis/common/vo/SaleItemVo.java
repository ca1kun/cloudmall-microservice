// 文件路径: D:/idea/workspace/demo/isdp/isdp-boot3Plus/mis-pos/src/main/java/edu/scau/mis/pos/vo/SaleItemVo.java
package edu.scau.mis.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleItemVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 【新增】商品ID，用于前端执行修改、删除等操作
     */
    private Long productId;

    private String itemSn;
    private String productName;
    private BigDecimal price;
    private int quantity;

    public BigDecimal getSubtotal() {
        if (price == null) {
            return BigDecimal.ZERO;
        }
        return price.multiply(new BigDecimal(this.quantity));
    }
}
