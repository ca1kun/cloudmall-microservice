package edu.scau.mis.ai.controller;

import edu.scau.mis.common.domain.ApiResult; // 你的公共结果类
import org.springframework.ai.chat.client.ChatClient; // ✅ 1.0.0-M1 新包名
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class ChatController {

    private final ChatClient chatClient;

    // ✅ 关键修改：注入 Builder，而不是直接注入 ChatClient
    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/chat")
    public ApiResult<String> chat(@RequestParam String msg) {
        // ✅ 关键修改：使用流式 API，而不是弃用的 call(String)
        String response = chatClient.prompt()
                .user(msg)
                .call()
                .content();

        return ApiResult.success(response);
    }
}