package edu.scau.mis.common.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Codex added: 渠道订单返回对象。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "渠道订单")
public class ChannelOrdersVo {
    private List<String> channels;
    private List<Integer> orders;
}
