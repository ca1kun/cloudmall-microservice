package edu.scau.mis.ai.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageVo {

    /**
     * user / assistant
     */
    private String role;

    /**
     * 消息内容
     */
    private String content;
}