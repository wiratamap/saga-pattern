package com.personal.librarykafkaproducer.orchestration.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.personal.librarykafkaproducer.orchestration.exception.OrchestrationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

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

    @Test
    void orchestrate_shouldNotInvokeKafkaTemplateSend_whenNoEventTopicsAreProvided() {
        String anEvent = "an_event";

        sagaOrchestrationService.orchestrate(anEvent, Collections.emptyList());

        verify(kafkaTemplate, never()).send(anyString(), eq(anEvent));
    }

    @Test
    void orchestrate_shouldThrowOrchestrationException_whenEventIsEmptyOrNull() {
        String anEvent = "an_event";

        Executable orchestrateAction = () -> sagaOrchestrationService.orchestrate(null, Collections.emptyList());

        assertThrows(OrchestrationException.class, orchestrateAction);
        verify(kafkaTemplate, never()).send(anyString(), eq(anEvent));
    }
}
