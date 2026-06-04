package edu.scau.mis.ai.service;

import edu.scau.mis.ai.dto.ChatRequest;
import edu.scau.mis.ai.vo.ChatMessageVo;
import reactor.core.publisher.Flux;

import java.util.List;

public interface AiChatService {

    /**
     * 普通聊天
     */
    String chat(ChatRequest request);

    /**
     * 流式聊天
     */
    Flux<String> streamChat(ChatRequest request);

    /**
     * 查询会话历史
     */
    List<ChatMessageVo> getHistory(String sessionId);

    /**
     * 清空会话历史
     */
    void clearHistory(String sessionId);
}