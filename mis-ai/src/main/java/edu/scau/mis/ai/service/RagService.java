package edu.scau.mis.ai.service;

import edu.scau.mis.ai.dto.RagDocumentDTO;
import edu.scau.mis.ai.enums.AiIntentType;

import java.util.List;

public interface RagService {
    List<RagDocumentDTO> retrieve(String query, AiIntentType intentType);
}
