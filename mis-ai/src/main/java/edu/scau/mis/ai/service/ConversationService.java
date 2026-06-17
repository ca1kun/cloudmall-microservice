package edu.scau.mis.ai.service;

import edu.scau.mis.ai.dto.ConversationMessageDTO;

import java.util.List;

public interface ConversationService {
    List<ConversationMessageDTO> getRecentMessages(String sessionId);

    void saveUserMessage(String sessionId, String message);

    void saveAssistantMessage(String sessionId, String message);

    void clearSession(String sessionId);
}
