package com.personal.sagapattern.orchestration.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SagaOrchestrationServiceTest {

    private SagaOrchestrationService sagaOrchestrationService;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @BeforeEach
    void setUp() {
        sagaOrchestrationService = new SagaOrchestrationService(kafkaTemplate);
    }

    @Test
    void orchestrate_shouldInvokeKafkaTemplateSendThreeTimes_whenEventTopicsIsTwo() {
        String anEvent = "an_event";
        List<String> eventTopics = Arrays.asList("EVENT_TOP_UP", "SURROUNDING_NOTIFICATION", "TOP_UP_NOTIFICATION");

        sagaOrchestrationService.orchestrate(anEvent, eventTopics);

        verify(kafkaTemplate, times(3)).send(anyString(), eq(anEvent));
    }
}
