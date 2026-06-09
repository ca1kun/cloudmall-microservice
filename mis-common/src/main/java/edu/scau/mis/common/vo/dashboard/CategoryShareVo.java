package edu.scau.mis.common.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Codex added: 品类占比返回对象。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "品类占比")
public class CategoryShareVo {
    private List<NameValueItemVo> data;
}
