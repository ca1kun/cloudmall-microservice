package edu.scau.mis.ai.dto;

import lombok.Data;

@Data
public class ProductRecommendRequest {

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 用户需求
     */
    private String requirement;
}