package edu.scau.mis.ai.dto;

import lombok.Data;

import java.util.List;

@Data
public class AiChatResponse {
    private String sessionId;
    private String intentType;
    private String answer;
    private Object data;
    private List<RagDocumentDTO> ragDocs;
    private List<String> suggestions;
}
