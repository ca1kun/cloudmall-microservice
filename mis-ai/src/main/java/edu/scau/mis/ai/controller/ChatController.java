package edu.scau.mis.ai.controller;

import edu.scau.mis.common.domain.ApiResult;
import lombok.Getter;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/ai")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem("""
                        你是 MIS 系统的 AI 助手。
                        回答要简洁、准确、可落地。
                        """)
                .build();
    }

    @PostMapping("/chat")
    public ApiResult<String> chat(@RequestBody ChatRequest request) {
        String response = chatClient.prompt()
                .user(request.getMsg())
                .call()
                .content();

        return ApiResult.success(response);
    }

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestBody ChatRequest request) {
        return chatClient.prompt()
                .user(request.getMsg())
                .stream()
                .content();
    }

    @Getter
    public static class ChatRequest {
        private String sessionId;
        private String msg;

        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }
}