package edu.scau.mis.ai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.scau.mis.ai.dto.AiChatRequest;
import edu.scau.mis.ai.dto.AiChatResponse;
import edu.scau.mis.ai.service.AssistantService;
import edu.scau.mis.common.domain.ApiResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/ai/assistant")
public class AssistantChatController {

    @Autowired
    private AssistantService assistantService;
    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/chat")
    public ApiResult<AiChatResponse> chat(@Valid @RequestBody AiChatRequest request) {
        return ApiResult.success(assistantService.chat(request));
    }

    /**
     * SSE 流式接口 — 直接写 HttpServletResponse 并 flush，
     * 避免 SseEmitter / Flux 在 Vite 代理下被缓冲导致前端收不到事件。
     */
    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public void streamChat(@Valid @RequestBody AiChatRequest request,
                           HttpServletRequest httpRequest,
                           HttpServletResponse httpResponse) throws IOException {
        // 开启 Servlet 异步，避免阻塞 Tomcat 线程
        httpRequest.startAsync();
        httpRequest.getAsyncContext().setTimeout(120_000);

        // 设置 SSE 响应头
        httpResponse.setContentType("text/event-stream");
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setHeader("Cache-Control", "no-cache");
        httpResponse.setHeader("Connection", "keep-alive");
        httpResponse.setHeader("X-Accel-Buffering", "no");
        httpResponse.setBufferSize(0);

        PrintWriter writer = httpResponse.getWriter();

        // 保存主线程的请求上下文，供异步线程使用（Feign token 透传依赖此上下文）
        ServletRequestAttributes parentAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        // 在新线程中消费 Flux，逐条写入 SSE 事件
        new Thread(() -> {
            // 设置请求上下文到异步线程，使 Feign 拦截器能获取 Authorization header
            RequestContextHolder.setRequestAttributes(parentAttributes, true);
            try {
                for (ServerSentEvent<String> sse : assistantService.streamChat(request).toIterable()) {
                    if (sse.event() != null) {
                        writer.write("event:" + sse.event() + "\n");
                    }
                    writer.write("data:" + sse.data() + "\n\n");
                    writer.flush();
                }
            } catch (Exception e) {
                log.error("Stream chat failed, sessionId={}", request.getSessionId(), e);
                try {
                    writer.write("event:error\n");
                    writer.write("data:AI 服务暂时不可用，请稍后重试\n\n");
                    writer.flush();
                } catch (Exception ignored) {
                }
            } finally {
                httpRequest.getAsyncContext().complete();
            }
        }).start();
    }
}
