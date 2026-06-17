package edu.scau.mis.ai.service;

import edu.scau.mis.ai.dto.AiChatRequest;
import edu.scau.mis.ai.dto.IntentDetectResult;
import edu.scau.mis.ai.enums.AiIntentType;

public interface ToolDispatcherService {
    Object dispatch(AiIntentType intentType, AiChatRequest request, IntentDetectResult intentResult);
}
