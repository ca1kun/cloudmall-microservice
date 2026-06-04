package edu.scau.mis.ai.dto;

import lombok.Data;

@Data
public class ChatRequest {

    /**
     * 会话 ID
     * 同一个 sessionId 代表同一轮连续对话
     */
    private String sessionId;

    /**
     * 用户消息
     */
    private String msg;
}