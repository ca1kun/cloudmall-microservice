package edu.scau.mis.ai.service;

import reactor.core.publisher.Flux;

public interface LlmService {
    String chat(String systemPrompt, String userPrompt);

    Flux<String> streamChat(String systemPrompt, String userPrompt);
}
