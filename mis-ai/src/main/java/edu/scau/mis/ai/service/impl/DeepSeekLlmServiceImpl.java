package edu.scau.mis.ai.service.impl;

import edu.scau.mis.ai.service.LlmService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class DeepSeekLlmServiceImpl implements LlmService {

    private final ChatClient chatClient;

    public DeepSeekLlmServiceImpl(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @Override
    public String chat(String systemPrompt, String userPrompt) {
        return chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .call()
                .content();
    }

    @Override
    public Flux<String> streamChat(String systemPrompt, String userPrompt) {
        log.info("DeepSeek stream chat started");
        return chatClient.prompt()
                .system(systemPrompt)
                .user(userPrompt)
                .stream()
                .content()
                .doOnNext(chunk -> log.debug("DeepSeek stream chunk: [{}]", chunk))
                .doOnComplete(() -> log.info("DeepSeek stream complete"))
                .doOnError(e -> log.error("DeepSeek stream error", e));
    }
}
