package edu.scau.mis.ai.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.scau.mis.ai.constant.AiConstants;
import edu.scau.mis.ai.dto.AiChatRequest;
import edu.scau.mis.ai.dto.AiChatResponse;
import edu.scau.mis.ai.dto.ConversationMessageDTO;
import edu.scau.mis.ai.dto.RagDocumentDTO;
import edu.scau.mis.ai.function.CartTools;
import edu.scau.mis.ai.function.CouponTools;
import edu.scau.mis.ai.function.OrderTools;
import edu.scau.mis.ai.function.ProductSearchTools;
import edu.scau.mis.ai.service.AssistantService;
import edu.scau.mis.ai.service.ConversationService;
import edu.scau.mis.ai.service.RagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于 Spring AI Tool Calling 的 AI 助手服务
 *
 * 核心变化：不再用硬编码关键词做意图识别，而是让 LLM 自己理解用户意图，
 * 通过 Tool Calling 自主决定调用哪个工具（商品搜索、购物车、订单、优惠券）。
 * LLM 知道"海飞丝"是洗发水品牌，会自动设置 keyword="海飞丝" categoryName="洗护" 来搜索。
 */
@Slf4j
@Service
public class AssistantServiceImpl implements AssistantService {

    @Autowired
    private ConversationService conversationService;
    @Autowired
    private RagService ragService;
    @Autowired
    private ChatClient.Builder chatClientBuilder;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductSearchTools productSearchTools;
    @Autowired
    private CartTools cartTools;
    @Autowired
    private OrderTools orderTools;
    @Autowired
    private CouponTools couponTools;

    private static final String SYSTEM_PROMPT = """
            你是 CloudMall 商城的 AI 智能助手，可以帮助用户完成以下操作：

            1. **商品搜索与推荐**：根据用户需求搜索商品，提供推荐和对比。
               - 用户提到品牌名（如"海飞丝"、"飘柔"、"潘婷"等）时，keyword 填品牌名，categoryName 填对应的分类（如"洗护"）
               - 用户提到功能需求（如"去屑"、"控油"）时，keyword 填功能词，categoryName 填对应分类
               - 用户提到价格范围时，设置 minPrice 和 maxPrice

            2. **购物车操作**：查看购物车、添加商品、移除商品。
               - 添加商品时，如果用户只说了商品名没给 productId，先调用 searchProducts 获取 productId，再调用 addToCart
               - 移除商品时，可以用 keyword 在购物车中匹配商品名

            3. **订单查询与取消**：查询订单详情、取消订单。

            4. **优惠券查询与领取**：查看可用优惠券、领取优惠券。

            回答要求：
            - 优先基于工具返回的真实数据回答，不要编造商品、价格、库存等信息
            - 如果工具返回空结果，诚实告知用户并建议调整搜索条件
            - 语气简洁友好，像专业的导购助手
            - 涉及退换货、发票、售后等政策问题时，参考提供的知识库内容
            """;

    @Override
    public AiChatResponse chat(AiChatRequest request) {
        try {
            conversationService.saveUserMessage(request.getSessionId(), request.getMessage());
            List<ConversationMessageDTO> history = conversationService.getRecentMessages(request.getSessionId());

            List<RagDocumentDTO> ragDocs = ragService.retrieve(request.getMessage(),
                    edu.scau.mis.ai.enums.AiIntentType.UNKNOWN);

            String userPrompt = buildUserPrompt(request, ragDocs, history);

            ChatClient chatClient = chatClientBuilder
                    .defaultSystem(SYSTEM_PROMPT)
                    .defaultTools(productSearchTools, cartTools, orderTools, couponTools)
                    .build();

            String answer = chatClient.prompt()
                    .user(userPrompt)
                    .call()
                    .content();

            if (answer == null || answer.isBlank()) {
                answer = AiConstants.DEFAULT_EMPTY_RESPONSE;
            }

            conversationService.saveAssistantMessage(request.getSessionId(), answer);

            AiChatResponse response = new AiChatResponse();
            response.setSessionId(request.getSessionId());
            response.setIntentType("SMART");
            response.setAnswer(answer);
            response.setRagDocs(ragDocs);
            response.setSuggestions(Arrays.asList("推荐几款热门商品", "帮我查一下订单", "我能用哪些优惠券"));
            return response;
        } catch (Exception e) {
            log.error("AI chat failed, sessionId={}, message={}", request.getSessionId(), request.getMessage(), e);
            AiChatResponse errorResponse = new AiChatResponse();
            errorResponse.setSessionId(request.getSessionId());
            errorResponse.setIntentType("ERROR");
            errorResponse.setAnswer("抱歉，AI 服务暂时不可用，请稍后重试。");
            errorResponse.setSuggestions(Arrays.asList("推荐几款热门商品", "帮我查一下订单", "我能用哪些优惠券"));
            return errorResponse;
        }
    }

    @Override
    public Flux<ServerSentEvent<String>> streamChat(AiChatRequest request) {
        return Mono.fromCallable(() -> {
            conversationService.saveUserMessage(request.getSessionId(), request.getMessage());
            List<ConversationMessageDTO> history = conversationService.getRecentMessages(request.getSessionId());
            List<RagDocumentDTO> ragDocs = ragService.retrieve(request.getMessage(),
                    edu.scau.mis.ai.enums.AiIntentType.UNKNOWN);
            String userPrompt = buildUserPrompt(request, ragDocs, history);
            return new StreamContext(request, ragDocs, userPrompt);
        })
        .subscribeOn(Schedulers.boundedElastic())
        .flatMapMany(ctx -> {
            ServerSentEvent<String> metadataEvent = buildMetadataSseEvent(ctx);

            StringBuilder fullAnswer = new StringBuilder();

            ChatClient chatClient = chatClientBuilder
                    .defaultSystem(SYSTEM_PROMPT)
                    .defaultTools(productSearchTools, cartTools, orderTools, couponTools)
                    .build();

            Flux<ServerSentEvent<String>> textStream = chatClient.prompt()
                    .user(ctx.userPrompt)
                    .stream()
                    .content()
                    .map(chunk -> ServerSentEvent.<String>builder().data(chunk).build())
                    .doOnNext(sse -> {
                        if (sse.data() != null) fullAnswer.append(sse.data());
                    })
                    .doOnComplete(() -> {
                        String answer = !fullAnswer.isEmpty() ? fullAnswer.toString() : AiConstants.DEFAULT_EMPTY_RESPONSE;
                        conversationService.saveAssistantMessage(ctx.request.getSessionId(), answer);
                    })
                    .onErrorResume(e -> {
                        log.error("LLM stream error, sessionId={}", ctx.request.getSessionId(), e);
                        try {
                            String syncAnswer = chatClient.prompt()
                                    .user(ctx.userPrompt)
                                    .call()
                                    .content();
                            if (syncAnswer == null || syncAnswer.isBlank()) {
                                syncAnswer = "AI 服务暂时不可用，请稍后重试。";
                            }
                            final String answer = syncAnswer;
                            conversationService.saveAssistantMessage(ctx.request.getSessionId(), answer);
                            return Flux.fromArray(answer.split(""))
                                    .filter(s -> !s.isEmpty())
                                    .map(c -> ServerSentEvent.<String>builder().data(c).build());
                        } catch (Exception ex) {
                            log.error("Sync fallback also failed, sessionId={}", ctx.request.getSessionId(), ex);
                            return Mono.just(ServerSentEvent.<String>builder()
                                    .event("error")
                                    .data("AI 服务连接超时，请检查网络后重试。")
                                    .build());
                        }
                    });

            ServerSentEvent<String> doneEvent = buildDoneSseEvent();

            return Flux.concat(
                    Mono.just(metadataEvent),
                    textStream,
                    Mono.just(doneEvent)
            );
        })
        .onErrorResume(e -> {
            log.error("AI stream chat failed, sessionId={}", request.getSessionId(), e);
            return Mono.just(ServerSentEvent.<String>builder()
                    .event("error")
                    .data("AI 服务暂时不可用，请稍后重试")
                    .build());
        });
    }

    private String buildUserPrompt(AiChatRequest request, List<RagDocumentDTO> ragDocs,
                                   List<ConversationMessageDTO> history) {
        StringBuilder b = new StringBuilder();

        if (history != null && !history.isEmpty()) {
            b.append("【对话历史】\n");
            for (ConversationMessageDTO msg : history) {
                b.append(msg.getRole()).append(": ").append(msg.getContent()).append("\n");
            }
            b.append("\n");
        }

        b.append("【用户消息】").append(request.getMessage()).append("\n");
        b.append("【用户ID】").append(request.getMemberId()).append("\n");

        if (ragDocs != null && !ragDocs.isEmpty()) {
            b.append("【参考知识】\n");
            for (RagDocumentDTO doc : ragDocs) {
                b.append("- ").append(doc.getTitle()).append(": ").append(doc.getContent()).append("\n");
            }
            b.append("\n");
        }

        b.append("请根据用户消息，调用合适的工具获取数据，然后基于工具返回的真实数据回答用户。");
        return b.toString();
    }

    private ServerSentEvent<String> buildMetadataSseEvent(StreamContext ctx) {
        try {
            Map<String, Object> meta = new HashMap<>();
            meta.put("sessionId", ctx.request.getSessionId());
            meta.put("intentType", "SMART");
            meta.put("ragDocs", ctx.ragDocs);
            String json = objectMapper.writeValueAsString(meta);
            return ServerSentEvent.<String>builder().event("metadata").data(json).build();
        } catch (Exception e) {
            log.warn("buildMetadataSseEvent failed", e);
            return ServerSentEvent.<String>builder().event("metadata").data("{}").build();
        }
    }

    private ServerSentEvent<String> buildDoneSseEvent() {
        try {
            Map<String, Object> done = new HashMap<>();
            done.put("suggestions", Arrays.asList("推荐几款热门商品", "帮我查一下订单", "我能用哪些优惠券"));
            String json = objectMapper.writeValueAsString(done);
            return ServerSentEvent.<String>builder().event("done").data(json).build();
        } catch (Exception e) {
            log.warn("buildDoneSseEvent failed", e);
            return ServerSentEvent.<String>builder().event("done").data("{}").build();
        }
    }

    private record StreamContext(
            AiChatRequest request,
            List<RagDocumentDTO> ragDocs,
            String userPrompt
    ) {}
}
