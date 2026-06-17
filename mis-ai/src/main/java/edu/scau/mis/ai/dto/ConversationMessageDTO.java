package edu.scau.mis.ai.dto;

import edu.scau.mis.ai.enums.AiMessageRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationMessageDTO {
    private AiMessageRole role;
    private String content;
    private LocalDateTime createTime;
}
