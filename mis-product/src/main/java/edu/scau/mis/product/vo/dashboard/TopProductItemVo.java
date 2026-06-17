package edu.scau.mis.product.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Codex added: 热销商品排行项，由销售明细表聚合得到。
 */
@Data
@Schema(description = "热销商品排行项")
public class TopProductItemVo {
    private Integer rank;
    private String name;
    private Integer count;
}
