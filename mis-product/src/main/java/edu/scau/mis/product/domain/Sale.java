package edu.scau.mis.product.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import edu.scau.mis.common.domain.BaseEntity;
import edu.scau.mis.common.domain.Product;
import edu.scau.mis.product.enums.SaleStatusEnum;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor // 为MyBatis保留一个无参构造函数
public class Sale extends BaseEntity implements Serializable {
    @TableId(type = IdType.AUTO)

    @JsonSerialize(using = ToStringSerializer.class)
    private Long saleId;
    private String saleNo;

    private BigDecimal total;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date saleTime;

    private String status;
    private Long paymentId;
    @Getter
    @TableField(exist = false)
    private List<SaleItem> saleItems = new ArrayList<>();

    // 构造器：用于创建一个新的、干净的Sale对象
    public Sale(String saleNo) {
        this.saleNo = saleNo;
        this.saleTime = new Date();
        this.status = SaleStatusEnum.UNPAID.getCode();
        this.saleItems = new ArrayList<>();
        this.total = BigDecimal.ZERO;
    }

    // --- 业务方法 ---
    public void addItem(Product product, int quantity) {
        if (!SaleStatusEnum.UNPAID.getCode().equals(this.status) && !SaleStatusEnum.RESERVED.getCode().equals(this.status)) {
            throw new IllegalStateException("只有未支付或挂起的订单才能添加商品");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("商品数量必须大于0");
        }

        // 【修改】现在基于 productId 进行查找
        Optional<SaleItem> existingItem = this.saleItems.stream()
                .filter(item -> item.getProductId().equals(product.getProductId()))
                .findFirst();

        if (existingItem.isPresent()) {
            SaleItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            // 【修改】使用新的构造器创建SaleItem
            SaleItem newItem = new SaleItem(product, quantity);
            this.saleItems.add(newItem);
        }
        recalculateTotal();
    }

    public void changeItemQuantity(Long productId, int newQuantity) {
        // 【修改】查找逻辑不变
        Optional<SaleItem> itemToChange = this.saleItems.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (itemToChange.isPresent()) {
            if (newQuantity > 0) {
                itemToChange.get().setQuantity(newQuantity);
            } else {
                this.saleItems.remove(itemToChange.get());
            }
            recalculateTotal();
        }
    }

    public void removeItem(Long productId) {
        // 【修改】查找逻辑不变
        boolean removed = this.saleItems.removeIf(item -> item.getProductId().equals(productId));
        if (removed) {
            recalculateTotal();
        }
    }


    public void completePayment() {
        if (this.saleItems.isEmpty()) {
            throw new IllegalStateException("购物车为空，无法支付");
        }
        this.status = SaleStatusEnum.PAID.getCode();
    }

    public void recordPayment(Long paymentId) {
        if (paymentId == null) {
            throw new IllegalArgumentException("支付ID不能为空");
        }
        this.paymentId = paymentId;
    }

    public void hold() {
        if (this.saleItems.isEmpty()) {
            throw new IllegalStateException("当前没有商品，无法挂起");
        }
        this.status = SaleStatusEnum.RESERVED.getCode();
    }

    private void recalculateTotal() {
        // 【修改】计算总价的逻辑现在更简单、更健壮
        this.total = this.saleItems.stream()
                .filter(item -> item != null && item.getPrice() != null) // 只需检查price
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    // --- 数据加载与访问控制 ---

    public void loadItems(List<SaleItem> items) {
        this.saleItems = items;
    }

}
