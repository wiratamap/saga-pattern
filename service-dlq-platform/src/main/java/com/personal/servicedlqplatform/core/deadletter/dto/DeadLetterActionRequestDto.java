package com.personal.servicedlqplatform.core.deadletter.dto;

import java.util.List;

import com.personal.servicedlqplatform.orchestration.service.SagaOrchestrationService;

public enum DeadLetterActionRequestDto {
    DELETE {
        @Override
        public void commit(SagaOrchestrationService sagaOrchestrationService, String message, List<String> originTopics) {
            // Do nothing
        }
    },
    SEND_TO_ORIGIN_TOPIC {
        @Override
        public void commit(SagaOrchestrationService sagaOrchestrationService, String message, List<String> originTopics) {
            sagaOrchestrationService.orchestrate(message, originTopics);
        }
    };

    public abstract void commit(SagaOrchestrationService sagaOrchestrationService, String message,
            List<String> originTopics);
}
