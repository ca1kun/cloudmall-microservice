package edu.scau.mis.ai.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.scau.mis.ai.client.MockProductSearchClient;
import edu.scau.mis.ai.dto.ProductRecommendRequest;
import edu.scau.mis.ai.dto.ProductSearchCondition;
import edu.scau.mis.ai.service.ProductRecommendService;
import edu.scau.mis.ai.vo.ProductCandidateVo;
import edu.scau.mis.ai.vo.ProductRecommendItemVo;
import edu.scau.mis.ai.vo.ProductRecommendResponseVo;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductRecommendServiceImpl implements ProductRecommendService {

    private final ChatClient.Builder chatClientBuilder;
    private final ObjectMapper objectMapper;
    private final MockProductSearchClient productSearchClient;

    @Override
    public ProductRecommendResponseVo recommend(ProductRecommendRequest request) {
        checkRequest(request);

        ProductSearchCondition condition = extractCondition(request.getRequirement());

        List<ProductCandidateVo> candidates = productSearchClient.searchProducts(condition);

        if (candidates == null || candidates.isEmpty()) {
            ProductRecommendResponseVo empty = new ProductRecommendResponseVo();
            empty.setSummary("暂时没有找到符合你需求的商品，可以放宽预算或换个关键词试试。");
            empty.setItems(new ArrayList<>());
            return empty;
        }

        return generateRecommendResult(request.getRequirement(), candidates);
    }

    private void checkRequest(ProductRecommendRequest request) {
        if (request == null) {
            throw new RuntimeException("请求参数不能为空");
        }

        if (!StringUtils.hasText(request.getRequirement())) {
            throw new RuntimeException("用户需求不能为空");
        }
    }

    /**
     * 第一步：用 AI 从用户需求中提取查询条件
     */
    private ProductSearchCondition extractCondition(String requirement) {
        ChatClient chatClient = chatClientBuilder
                .defaultSystem("""
                        你是一个电商商品推荐需求解析助手。
                        你的任务是从用户需求中提取商品搜索条件。
                        只允许返回 JSON，不要返回解释，不要返回 markdown。
                        JSON 字段包括：
                        keyword: 商品关键词，例如耳机、手机、键盘
                        categoryName: 分类名称
                        minPrice: 最低价格，没有则为 null
                        maxPrice: 最高价格，没有则为 null
                        scene: 使用场景，例如学生、办公、运动、游戏
                        features: 用户关注的特性数组，例如续航、降噪、轻便、性价比
                        limit: 查询数量，默认 10
                        """)
                .build();

        String json = chatClient.prompt()
                .user("""
                        请从下面用户需求中提取商品搜索条件，只返回 JSON：
                        用户需求：%s
                        """.formatted(requirement))
                .call()
                .content();

        try {
            return objectMapper.readValue(cleanJson(json), ProductSearchCondition.class);
        } catch (Exception e) {
            ProductSearchCondition fallback = new ProductSearchCondition();
            fallback.setKeyword(requirement);
            fallback.setLimit(10);
            return fallback;
        }
    }

    /**
     * 第二步：让 AI 根据候选商品生成推荐理由
     */
    private ProductRecommendResponseVo generateRecommendResult(String requirement, List<ProductCandidateVo> candidates) {
        ChatClient chatClient = chatClientBuilder
                .defaultSystem("""
                        你是一个电商商品推荐助手。
                        你必须基于候选商品列表进行推荐，不能编造不存在的商品。
                        只允许返回 JSON，不要返回 markdown，不要返回解释。
                        JSON 格式：
                        {
                          "summary": "简短推荐总结",
                          "items": [
                            {
                              "productId": "商品ID",
                              "productName": "商品名称",
                              "price": 商品价格,
                              "imageUrl": "商品图片",
                              "reason": "推荐理由"
                            }
                          ]
                        }
                        最多推荐 3 个商品。
                        推荐理由要结合用户需求，例如预算、场景、功能、性价比。
                        """)
                .build();

        String candidateJson;
        try {
            candidateJson = objectMapper.writeValueAsString(candidates);
        } catch (Exception e) {
            throw new RuntimeException("候选商品序列化失败", e);
        }

        String json = chatClient.prompt()
                .user("""
                        用户需求：
                        %s

                        候选商品列表：
                        %s

                        请从候选商品中推荐最合适的商品，只返回 JSON。
                        """.formatted(requirement, candidateJson))
                .call()
                .content();

        try {
            return objectMapper.readValue(cleanJson(json), ProductRecommendResponseVo.class);
        } catch (Exception e) {
            return fallbackRecommend(requirement, candidates);
        }
    }

    /**
     * 兜底推荐：AI 返回 JSON 解析失败时，后端自己组装
     */
    private ProductRecommendResponseVo fallbackRecommend(String requirement, List<ProductCandidateVo> candidates) {
        ProductRecommendResponseVo response = new ProductRecommendResponseVo();
        response.setSummary("根据你的需求，推荐以下商品：");

        List<ProductRecommendItemVo> items = new ArrayList<>();

        for (int i = 0; i < Math.min(3, candidates.size()); i++) {
            ProductCandidateVo candidate = candidates.get(i);

            ProductRecommendItemVo item = new ProductRecommendItemVo();
            item.setProductId(candidate.getProductId());
            item.setProductName(candidate.getProductName());
            item.setPrice(candidate.getPrice());
            item.setImageUrl(candidate.getImageUrl());
            item.setReason("该商品符合你的需求：" + requirement + "。商品卖点：" + candidate.getSellingPoint());

            items.add(item);
        }

        response.setItems(items);
        return response;
    }

    /**
     * 清理 AI 可能返回的 ```json 包裹
     */
    private String cleanJson(String text) {
        if (text == null) {
            return "{}";
        }

        String result = text.trim();

        if (result.startsWith("```json")) {
            result = result.substring(7);
        }

        if (result.startsWith("```")) {
            result = result.substring(3);
        }

        if (result.endsWith("```")) {
            result = result.substring(0, result.length() - 3);
        }

        return result.trim();
    }
}