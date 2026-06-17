package edu.scau.mis.ai.dto;

import lombok.Data;

import java.util.Map;

@Data
public class RagDocumentDTO {
    private String docId;
    private String docType;
    private String title;
    private String content;
    private Map<String, Object> metadata;
}
