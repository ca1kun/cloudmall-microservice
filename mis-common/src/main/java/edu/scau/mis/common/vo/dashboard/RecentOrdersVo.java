package edu.scau.mis.common.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Codex added: 最新订单返回对象。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "最新订单")
public class RecentOrdersVo {
    private List<RecentOrderItemVo> list;
}
