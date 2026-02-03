package edu.scau.mis.common.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class StockLockDTO implements Serializable {
    private Long productId;
    private Integer count; // 购买数量
}