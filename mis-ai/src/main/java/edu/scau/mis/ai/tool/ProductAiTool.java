package edu.scau.mis.ai.tool;

import edu.scau.mis.ai.dto.IntentDetectResult;
import edu.scau.mis.ai.dto.ProductRecommendItemDTO;

import java.util.List;

public interface ProductAiTool {
    List<ProductRecommendItemDTO> recommendProducts(IntentDetectResult intentResult);

    Object getProductDetail(String keyword);
}
