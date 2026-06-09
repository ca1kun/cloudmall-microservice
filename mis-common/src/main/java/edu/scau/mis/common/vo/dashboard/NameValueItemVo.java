package edu.scau.mis.common.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Codex added: 商家大屏名称-数值通用返回项。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "名称数值项")
public class NameValueItemVo {
    private String name;
    private Integer value;
}
