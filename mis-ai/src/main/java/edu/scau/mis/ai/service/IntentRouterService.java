package edu.scau.mis.ai.service;

import edu.scau.mis.ai.dto.IntentDetectResult;

public interface IntentRouterService {
    IntentDetectResult detect(String message);
}
