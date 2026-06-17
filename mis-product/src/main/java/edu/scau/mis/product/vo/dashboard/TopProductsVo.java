package edu.scau.mis.product.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Codex added: 热销商品排行返回对象。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "热销商品排行")
public class TopProductsVo {
    private List<TopProductItemVo> list;
}
