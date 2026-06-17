package edu.scau.mis.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AiChatRequest {

    @NotBlank(message = "sessionId不能为空")
    private String sessionId;

    private Long memberId;

    @NotBlank(message = "message不能为空")
    private String message;

    private String scene;
}
