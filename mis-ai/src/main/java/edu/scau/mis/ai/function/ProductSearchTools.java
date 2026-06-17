package edu.scau.mis.ai.function;

import edu.scau.mis.ai.feign.RemoteProductFeign;
import edu.scau.mis.common.domain.ApiResult;
import edu.scau.mis.common.domain.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 商品搜索工具 - 供 LLM 通过 Tool Calling 自主调用
 * LLM 会理解用户意图，自动提取关键词和分类来搜索商品
 */
@Slf4j
@Component
public class ProductSearchTools {

    private final RemoteProductFeign remoteProductFeign;

    public ProductSearchTools(RemoteProductFeign remoteProductFeign) {
        this.remoteProductFeign = remoteProductFeign;
    }

    @Tool(description = "搜索商品。根据关键词、分类名、价格范围搜索商品列表。当用户提到品牌名（如海飞丝、飘柔、潘婷）时，keyword填品牌名，categoryName填对应分类（如洗护）。当用户提到功能需求（如去屑、控油）时，keyword填功能词。")
    public String searchProducts(
            @ToolParam(description = "搜索关键词，如商品名称、品牌名等，例如'海飞丝'、'手机'、'去屑洗发水'") String keyword,
            @ToolParam(description = "商品分类名称，如'手机'、'电脑'、'洗护'、'文具'、'食品'、'配件'") String categoryName,
            @ToolParam(description = "最低价格，单位元，没有则为null") Double minPrice,
            @ToolParam(description = "最高价格，单位元，没有则为null") Double maxPrice
    ) {
        log.info("searchProducts called: keyword={}, categoryName={}, minPrice={}, maxPrice={}", keyword, categoryName, minPrice, maxPrice);
        try {
            java.math.BigDecimal min = minPrice != null ? java.math.BigDecimal.valueOf(minPrice) : null;
            java.math.BigDecimal max = maxPrice != null ? java.math.BigDecimal.valueOf(maxPrice) : null;
            ApiResult<List<Product>> result = remoteProductFeign.searchProducts(keyword, categoryName, min, max);
            if (result == null || result.getData() == null || result.getData().isEmpty()) {
                return "未找到匹配的商品。建议调整搜索关键词或分类。";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("找到 ").append(result.getData().size()).append(" 个商品：\n");
            for (Product p : result.getData()) {
                sb.append("- 商品ID: ").append(p.getProductId())
                  .append(", 名称: ").append(p.getProductName())
                  .append(", 价格: ").append(p.getPrice())
                  .append(", 库存: ").append(p.getStock())
                  .append(", 描述: ").append(p.getProductDescription() != null ? p.getProductDescription() : "无")
                  .append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            log.warn("searchProducts failed", e);
            return "商品搜索失败: " + e.getMessage();
        }
    }
}
