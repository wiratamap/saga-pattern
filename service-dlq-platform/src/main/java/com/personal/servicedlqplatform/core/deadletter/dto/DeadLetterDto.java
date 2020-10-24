package com.personal.servicedlqplatform.core.deadletter.dto;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeadLetterDto {
    private String originalMessage;
    private String reason;
    private List<String> originTopics;

    public void setOriginalMessage(JsonNode originalMessage) {
        this.originalMessage = originalMessage.toString();
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setOriginTopics(List<String> originTopics) {
        this.originTopics = originTopics;
    }
}
