package edu.scau.mis.ai.service;

import edu.scau.mis.ai.dto.AiChatRequest;
import edu.scau.mis.ai.dto.ConversationMessageDTO;
import edu.scau.mis.ai.dto.RagDocumentDTO;
import edu.scau.mis.ai.enums.AiIntentType;

import java.util.List;

public interface PromptService {
    String buildSystemPrompt(AiIntentType intentType);

    String buildUserPrompt(AiChatRequest request, Object toolResult, List<RagDocumentDTO> ragDocs);

    String buildUserPrompt(AiChatRequest request, Object toolResult, List<RagDocumentDTO> ragDocs,
                           List<ConversationMessageDTO> history);
}
