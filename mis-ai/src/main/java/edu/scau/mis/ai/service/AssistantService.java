package edu.scau.mis.ai.service;

import edu.scau.mis.ai.dto.AiChatRequest;
import edu.scau.mis.ai.dto.AiChatResponse;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface AssistantService {
    AiChatResponse chat(AiChatRequest request);

    Flux<ServerSentEvent<String>> streamChat(AiChatRequest request);
}
