package edu.scau.mis.ai.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.scau.mis.ai.constant.AiConstants;
import edu.scau.mis.ai.constant.RedisKeyConstants;
import edu.scau.mis.ai.dto.ConversationMessageDTO;
import edu.scau.mis.ai.enums.AiMessageRole;
import edu.scau.mis.ai.service.ConversationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ConversationServiceImpl implements ConversationService {

    private static final Duration SESSION_TTL = Duration.ofDays(7);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<ConversationMessageDTO> getRecentMessages(String sessionId) {
        String value = stringRedisTemplate.opsForValue().get(buildKey(sessionId));
        if (value == null || value.isBlank()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(value, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            log.error("parse conversation failed, sessionId={}", sessionId, e);
            return new ArrayList<>();
        }
    }

    @Override
    public void saveUserMessage(String sessionId, String message) {
        saveMessage(sessionId, new ConversationMessageDTO(AiMessageRole.USER, message, LocalDateTime.now()));
    }

    @Override
    public void saveAssistantMessage(String sessionId, String message) {
        saveMessage(sessionId, new ConversationMessageDTO(AiMessageRole.ASSISTANT, message, LocalDateTime.now()));
    }

    @Override
    public void clearSession(String sessionId) {
        stringRedisTemplate.delete(buildKey(sessionId));
    }

    private void saveMessage(String sessionId, ConversationMessageDTO messageDTO) {
        List<ConversationMessageDTO> messages = getRecentMessages(sessionId);
        messages.add(messageDTO);
        if (messages.size() > AiConstants.DEFAULT_HISTORY_SIZE) {
            messages = new ArrayList<>(messages.subList(messages.size() - AiConstants.DEFAULT_HISTORY_SIZE, messages.size()));
        }
        try {
            stringRedisTemplate.opsForValue().set(buildKey(sessionId), objectMapper.writeValueAsString(messages), SESSION_TTL);
        } catch (JsonProcessingException e) {
            log.error("save conversation failed, sessionId={}", sessionId, e);
        }
    }

    private String buildKey(String sessionId) {
        return RedisKeyConstants.AI_CONVERSATION_KEY_PREFIX + sessionId;
    }
}
