package edu.scau.mis.ai.controller;

import edu.scau.mis.ai.dto.ChatRequest;
import edu.scau.mis.ai.service.AiChatService;
import edu.scau.mis.ai.vo.ChatMessageVo;
import edu.scau.mis.common.domain.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class ChatController {

    private final AiChatService aiChatService;

    /**
     * 普通聊天，带上下文
     */
    @PostMapping("/chat")
    public ApiResult<String> chat(@RequestBody ChatRequest request) {
        String response = aiChatService.chat(request);
        return ApiResult.success(response);
    }

    /**
     * 流式聊天，带上下文
     */
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestBody ChatRequest request) {
        return aiChatService.streamChat(request);
    }

    /**
     * 查看某个会话历史
     */
    @GetMapping("/session/{sessionId}")
    public ApiResult<List<ChatMessageVo>> getHistory(@PathVariable String sessionId) {
        return ApiResult.success(aiChatService.getHistory(sessionId));
    }

    /**
     * 清空某个会话历史
     */
    @DeleteMapping("/session/{sessionId}")
    public ApiResult<String> clearHistory(@PathVariable String sessionId) {
        aiChatService.clearHistory(sessionId);
        return ApiResult.success("清空成功");
    }
}