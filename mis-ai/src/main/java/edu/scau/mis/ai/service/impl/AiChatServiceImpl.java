package edu.scau.mis.ai.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.scau.mis.ai.dto.ChatRequest;
import edu.scau.mis.ai.service.AiChatService;
import edu.scau.mis.ai.vo.ChatMessageVo;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiChatServiceImpl implements AiChatService {

    private final ChatClient.Builder chatClientBuilder;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 每个会话最多保存最近 20 条消息。
     * 也就是 user + assistant 合起来 20 条，大约 10 轮对话。
     */
    private static final int MAX_HISTORY_SIZE = 20;

    /**
     * 会话过期时间：7 天
     */
    private static final Duration SESSION_TTL = Duration.ofDays(7);

    private static final String SESSION_KEY_PREFIX = "ai:chat:session:";

    @Override
    public String chat(ChatRequest request) {
        checkRequest(request);

        String redisKey = buildSessionKey(request.getSessionId());

        List<ChatMessageVo> history = loadHistory(redisKey);

        String prompt = buildPrompt(history, request.getMsg());

        ChatClient chatClient = chatClientBuilder
                .defaultSystem("""
                        你是 MIS 系统的 AI 助手。
                        回答要简洁、准确、可落地。
                        如果用户的问题涉及系统开发、Java、Spring Boot、数据库、Redis、Docker、Nacos、RabbitMQ，
                        请优先给出可以直接操作的方案。
                        """)
                .build();

        String answer = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        history.add(new ChatMessageVo("user", request.getMsg()));
        history.add(new ChatMessageVo("assistant", answer));

        saveHistory(redisKey, history);

        return answer;
    }

    @Override
    public Flux<String> streamChat(ChatRequest request) {
        checkRequest(request);

        String redisKey = buildSessionKey(request.getSessionId());

        List<ChatMessageVo> history = loadHistory(redisKey);
        String prompt = buildPrompt(history, request.getMsg());

        StringBuilder answerBuilder = new StringBuilder();

        ChatClient chatClient = chatClientBuilder
                .defaultSystem("""
                        你是 MIS 系统的 AI 助手。
                        回答要简洁、准确、可落地。
                        """)
                .build();

        return chatClient.prompt()
                .user(prompt)
                .stream()
                .content()
                .doOnNext(answerBuilder::append)
                .doOnComplete(() -> {
                    history.add(new ChatMessageVo("user", request.getMsg()));
                    history.add(new ChatMessageVo("assistant", answerBuilder.toString()));
                    saveHistory(redisKey, history);
                });
    }

    @Override
    public List<ChatMessageVo> getHistory(String sessionId) {
        if (!StringUtils.hasText(sessionId)) {
            throw new RuntimeException("sessionId 不能为空");
        }

        String redisKey = buildSessionKey(sessionId);
        return loadHistory(redisKey);
    }

    @Override
    public void clearHistory(String sessionId) {
        if (!StringUtils.hasText(sessionId)) {
            throw new RuntimeException("sessionId 不能为空");
        }

        String redisKey = buildSessionKey(sessionId);
        stringRedisTemplate.delete(redisKey);
    }

    private void checkRequest(ChatRequest request) {
        if (request == null) {
            throw new RuntimeException("请求参数不能为空");
        }

        if (!StringUtils.hasText(request.getSessionId())) {
            throw new RuntimeException("sessionId 不能为空");
        }

        if (!StringUtils.hasText(request.getMsg())) {
            throw new RuntimeException("消息内容不能为空");
        }
    }

    private String buildSessionKey(String sessionId) {
        String username = getCurrentUsername();
        return SESSION_KEY_PREFIX + username + ":" + sessionId;
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !StringUtils.hasText(authentication.getName())) {
            return "anonymous";
        }

        return authentication.getName();
    }

    private List<ChatMessageVo> loadHistory(String redisKey) {
        String json = stringRedisTemplate.opsForValue().get(redisKey);

        if (!StringUtils.hasText(json)) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(json, new TypeReference<List<ChatMessageVo>>() {});
        } catch (Exception e) {
            throw new RuntimeException("读取会话历史失败", e);
        }
    }

    private void saveHistory(String redisKey, List<ChatMessageVo> history) {
        List<ChatMessageVo> latestHistory = trimHistory(history);

        try {
            String json = objectMapper.writeValueAsString(latestHistory);
            stringRedisTemplate.opsForValue().set(redisKey, json, SESSION_TTL);
        } catch (Exception e) {
            throw new RuntimeException("保存会话历史失败", e);
        }
    }

    private List<ChatMessageVo> trimHistory(List<ChatMessageVo> history) {
        if (history.size() <= MAX_HISTORY_SIZE) {
            return history;
        }

        return new ArrayList<>(history.subList(history.size() - MAX_HISTORY_SIZE, history.size()));
    }

    private String buildPrompt(List<ChatMessageVo> history, String currentMsg) {
        StringBuilder sb = new StringBuilder();

        sb.append("下面是用户和助手之前的对话历史。");
        sb.append("请结合历史上下文回答用户最新问题。");
        sb.append("如果历史对话和当前问题无关，可以忽略历史。\n\n");

        if (history == null || history.isEmpty()) {
            sb.append("【历史对话】无\n\n");
        } else {
            sb.append("【历史对话】\n");

            for (ChatMessageVo message : history) {
                if ("user".equals(message.getRole())) {
                    sb.append("用户：").append(message.getContent()).append("\n");
                } else if ("assistant".equals(message.getRole())) {
                    sb.append("助手：").append(message.getContent()).append("\n");
                }
            }

            sb.append("\n");
        }

        sb.append("【用户最新问题】\n");
        sb.append(currentMsg);

        return sb.toString();
    }
}