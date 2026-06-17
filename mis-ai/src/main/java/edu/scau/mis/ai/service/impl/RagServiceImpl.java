package edu.scau.mis.ai.service.impl;

import edu.scau.mis.ai.dto.ProductKnowledgeDocDTO;
import edu.scau.mis.ai.dto.RagDocumentDTO;
import edu.scau.mis.ai.enums.AiIntentType;
import edu.scau.mis.ai.feign.RemoteProductFeign;
import edu.scau.mis.ai.service.RagService;
import edu.scau.mis.common.domain.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class RagServiceImpl implements RagService {

    private static final List<RagDocumentDTO> STATIC_DOCUMENTS = buildStaticDocs();

    @Autowired
    private RemoteProductFeign remoteProductFeign;

    @Override
    public List<RagDocumentDTO> retrieve(String query, AiIntentType intentType) {
        try {
            List<RagScoreItem> items = new ArrayList<>();

            for (RagDocumentDTO doc : STATIC_DOCUMENTS) {
                double s = scoreStaticDoc(doc, query, intentType);
                if (s > 0) items.add(new RagScoreItem(doc, s));
            }

            if (intentType == AiIntentType.PRODUCT_RECOMMEND || intentType == AiIntentType.PRODUCT_QA) {
                try {
                    items.addAll(retrieveProductDocs(query, intentType));
                } catch (Exception e) {
                    log.warn("Product docs retrieval failed, fallback to static only. query={}", query, e);
                }
            }

            return items.stream()
                    .filter(i -> i.score() > 0)
                    .sorted(Comparator.comparingDouble(RagScoreItem::score).reversed())
                    .limit(intentType == AiIntentType.POLICY_QA ? 3 : 5)
                    .map(RagScoreItem::document)
                    .toList();
        } catch (Exception e) {
            log.error("RAG retrieve failed, return empty list. query={}", query, e);
            return List.of();
        }
    }

    /**
     * 纯关键词匹配的商品检索，不依赖 embedding
     */
    private List<RagScoreItem> retrieveProductDocs(String query, AiIntentType intentType) {
        String category = detectCategory(query);
        ApiResult<List<ProductKnowledgeDocDTO>> featureResult;
        try {
            featureResult = remoteProductFeign.getFeatureDocs(query, category, 8);
        } catch (Exception e) {
            log.warn("getFeatureDocs failed, query={}, category={}", query, category, e);
            return List.of();
        }

        if (featureResult == null || featureResult.getData() == null || featureResult.getData().isEmpty()) {
            return List.of();
        }

        List<RagScoreItem> items = new ArrayList<>();
        List<String> queryTokens = tokenize(query);

        for (ProductKnowledgeDocDTO doc : featureResult.getData()) {
            RagDocumentDTO ragDoc = toRagDoc(doc);
            double score = scoreProductDoc(ragDoc, query, queryTokens, intentType, doc);
            items.add(new RagScoreItem(ragDoc, score));
        }
        return items;
    }

    /**
     * 商品文档评分：关键词命中 + 字段权重
     */
    private double scoreProductDoc(RagDocumentDTO doc, String query, List<String> queryTokens,
                                   AiIntentType intentType, ProductKnowledgeDocDTO raw) {
        double score = scoreStaticDoc(doc, query, intentType);

        // 对商品特征字段做额外关键词匹配加分
        String searchText = joinFields(raw.getProductName(), raw.getCategoryName(), raw.getBrand(),
                raw.getPriceBand(), raw.getSceneTags(), raw.getFeatureTags(), raw.getFeatureSummary(), raw.getSearchText());
        String lowerSearch = searchText.toLowerCase(Locale.ROOT);

        for (String token : queryTokens) {
            if (token.isBlank()) continue;
            String lower = token.toLowerCase(Locale.ROOT);
            if (lowerSearch.contains(lower)) {
                score += 5;
            }
        }

        // 商品特征文档额外加分
        if ("product_feature".equals(doc.getDocType())) score += 2;
        return score;
    }

    private List<String> tokenize(String text) {
        if (text == null || text.isBlank()) return List.of();
        Set<String> tokens = new LinkedHashSet<>();
        // 简单分词：按标点和空格拆分，保留中文连续段
        String[] parts = text.replaceAll("[\uFF0C\u3002\uFF01\uFF1F\u3001\uFF1B\uFF1A\u201C\u201D\u2018\u2019\uFF08\uFF09\\[\\]\\{\\}\\s]+", " ").split("\\s+");
        for (String p : parts) {
            if (!p.isBlank()) tokens.add(p.trim());
        }
        // 额外提取 2-gram 用于模糊匹配
        String noSpace = text.replaceAll("\\s+", "");
        for (int i = 0; i < noSpace.length() - 1; i++) {
            tokens.add(noSpace.substring(i, i + 2));
        }
        return new ArrayList<>(tokens);
    }

    private String joinFields(String... fields) {
        StringBuilder sb = new StringBuilder();
        for (String f : fields) {
            if (f != null && !f.isBlank()) {
                sb.append(f).append(" ");
            }
        }
        return sb.toString();
    }

    private double scoreStaticDoc(RagDocumentDTO doc, String query, AiIntentType intentType) {
        String text = (safe(query) + " " + intentType.name()).toLowerCase(Locale.ROOT);
        String content = (safe(doc.getTitle()) + " " + safe(doc.getContent())).toLowerCase(Locale.ROOT);
        double score = 0;
        for (String token : keywordHints()) {
            if (text.contains(token) && content.contains(token)) score += 3;
        }
        if (intentType == AiIntentType.POLICY_QA && "policy".equals(doc.getDocType())) score += 3;
        if ((intentType == AiIntentType.PRODUCT_RECOMMEND || intentType == AiIntentType.PRODUCT_QA)
                && ("guide".equals(doc.getDocType()) || "product_feature".equals(doc.getDocType()))) score += 4;
        if ("product_feature".equals(doc.getDocType())) score += 2;
        return score;
    }

    private RagDocumentDTO toRagDoc(ProductKnowledgeDocDTO doc) {
        RagDocumentDTO dto = new RagDocumentDTO();
        dto.setDocId("product-feature-" + doc.getProductId());
        dto.setDocType("product_feature");
        dto.setTitle(doc.getProductName());
        dto.setContent(buildProductContent(doc));
        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("source", "product-feature-store");
        meta.put("productId", doc.getProductId());
        meta.put("categoryName", doc.getCategoryName());
        meta.put("brand", doc.getBrand());
        meta.put("priceBand", doc.getPriceBand());
        meta.put("sceneTags", doc.getSceneTags());
        meta.put("featureTags", doc.getFeatureTags());
        dto.setMetadata(meta);
        return dto;
    }

    private String buildProductContent(ProductKnowledgeDocDTO doc) {
        return "商品名: " + safe(doc.getProductName())
            + "；分类: " + safe(doc.getCategoryName())
            + "；品牌: " + safe(doc.getBrand())
            + "；价位段: " + safe(doc.getPriceBand())
            + "；场景标签: " + safe(doc.getSceneTags())
            + "；特征标签: " + safe(doc.getFeatureTags())
            + "；摘要: " + safe(doc.getFeatureSummary())
            + "；检索文本: " + safe(doc.getSearchText());
    }

    private String detectCategory(String query) {
        if (query == null || query.isBlank()) return null;
        String[] cats = {"手机", "电脑", "笔记本", "文具", "洗发", "洗护", "图书", "食品"};
        for (String c : cats) if (query.contains(c)) return c;
        return null;
    }

    private List<String> keywordHints() {
        return List.of("退货","退款","发票","售后","物流","配送","手机","游戏","拍照","优惠券","购物车","结算","续航","快充","电脑","文具","领券","下单","取消","订单","购买","加入","删除");
    }

    // ---- static knowledge base ----
    private static List<RagDocumentDTO> buildStaticDocs() {
        return List.of(
            doc("policy-return","policy","退换货规则","签收后7天内可申请无理由退货，需商品完好不影响二次销售。数码类激活后不支持无理由退货。质量问题需提供照片或检测报告。"),
            doc("policy-invoice","policy","发票规则","支持电子发票，抬头支持个人和企业。企业需填写税号。已开发票的订单退款需先冲红或作废。"),
            doc("policy-shipping","policy","配送规则","普通商品付款后24小时内发货。活动高峰可能延迟。偏远地区以承运商时效为准。可通过订单详情查看物流。"),
            doc("policy-after-sale","policy","售后规则","售后类型包括退款、退货退款、换货和维修。用户应提供订单号、问题描述和商品照片。"),
            doc("guide-phone","guide","手机导购","选手机看预算、性能、屏幕、拍照、续航。游戏用户优先处理器和散热；拍照用户优先主摄和防抖；学生优先续航和价格。"),
            doc("guide-laptop","guide","笔记本导购","选笔记本看预算、用途、处理器、内存、屏幕。办公轻薄本优先续航；游戏本优先显卡和散热；学生本优先性价比。"),
            doc("guide-coupon","guide","优惠券使用","确认使用门槛、有效期、适用商品。快过期券优先用于刚需商品。不建议为了用券买不需要的商品。"),
            doc("guide-cart","guide","购物车结算","结算前确认库存、当前价格、数量和可用优惠券。若价格变化以结算页为准。"),
            doc("guide-order","guide","下单指南","下单时确认商品、数量、收货地址。提交后可在订单页查看状态、申请售后或联系客服。"),
            doc("guide-cancel","guide","取消订单","未发货订单可直接取消。已发货订单需联系客服。取消后金额退回原支付方式。"),
            doc("guide-stationery","guide","文具导购","选文具看用途、材质、品牌。学生文具优先性价比和耐用性；办公文具优先品质和环保认证；笔记本选纸张克重和装订方式。"),
            doc("guide-shampoo","guide","洗护导购","选洗发水看发质需求：油性选控油清爽型，干性选滋润修复型，受损发质选修护型。注意看成分表，避免刺激性成分。")
        );
    }

    private static RagDocumentDTO doc(String id, String type, String title, String content) {
        RagDocumentDTO dto = new RagDocumentDTO();
        dto.setDocId(id); dto.setDocType(type); dto.setTitle(title); dto.setContent(content);
        Map<String, Object> meta = new HashMap<>(); meta.put("source", "local-knowledge-base");
        dto.setMetadata(meta);
        return dto;
    }

    private String safe(String v) { return v == null ? "" : v; }
    private record RagScoreItem(RagDocumentDTO document, double score) {}
}
