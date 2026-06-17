package edu.scau.mis.ai.controller;

import edu.scau.mis.ai.dto.ConversationMessageDTO;
import edu.scau.mis.ai.service.ConversationService;
import edu.scau.mis.common.domain.ApiResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ai/conversation")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @GetMapping("/{sessionId}")
    public ApiResult<List<ConversationMessageDTO>> getMessages(@PathVariable("sessionId") String sessionId) {
        return ApiResult.success(conversationService.getRecentMessages(sessionId));
    }

    @DeleteMapping("/{sessionId}")
    public ApiResult<String> clearMessages(@PathVariable("sessionId") String sessionId) {
        conversationService.clearSession(sessionId);
        return ApiResult.success("会话已清空");
    }
}
