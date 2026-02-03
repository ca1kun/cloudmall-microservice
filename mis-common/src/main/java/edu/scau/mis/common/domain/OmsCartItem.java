package edu.scau.mis.common.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("oms_cart_item")
public class OmsCartItem implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    // 用户ID
    private Long memberId;

    // 商品ID
    private Long productId;

    // 购买数量
    private Integer quantity;

    // 添加购物车时的价格 (快照)
    private BigDecimal price;

    // 商品名称
    private String productName;

    // 商品图片
    private String productPic;

    // 创建时间
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createDate;

    // 修改时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date modifyDate;

    // 👇 新增：当前最新价格 (数据库里没有，需要查询填充)
    @TableField(exist = false) // 告诉 MP 这不是数据库字段
    private BigDecimal currentPrice;
}