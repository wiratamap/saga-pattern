package com.personal.sagapattern.consumer;

import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.common.model.Disposable;
import com.personal.sagapattern.core.enumeration.Status;
import com.personal.sagapattern.core.model.dto.DeadLetterMessage;
import com.personal.sagapattern.core.model.dto.TopUpEventResult;
import com.personal.sagapattern.core.service.WalletService;
import com.personal.sagapattern.orchestration.service.SagaOrchestrationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventTopUpFailedListenerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private EventTopUpFailedListener eventTopUpFailedListener;

    @Mock
    private WalletService walletService;

    @Mock
    private SagaOrchestrationService sagaOrchestrationService;

    private final List<String> deadLetterTopics = Collections.singletonList("EVENT_TOP_UP_DEAD_LETTER");
    private final List<String> originTopics = Arrays.asList("EVENT_TOP_UP", "SURROUNDING_NOTIFICATION",
            "TOP_UP_NOTIFICATION");

    @BeforeEach
    void setUp() {
        eventTopUpFailedListener = new EventTopUpFailedListener(walletService, sagaOrchestrationService,
                deadLetterTopics, originTopics);
    }

    @Test
    void consume_shouldInvokeUpdateStatusWithTopUpRequestAndFailedStatus() throws JsonProcessingException {
        TopUpEventResult topUpEventResult = TopUpEventResult.builder().eventId(UUID.randomUUID()).cif("000000001")
                .amount(10000).wallet("GO-PAY").destinationOfFund("00000000").reason("REASON").build();
        String message = objectMapper.writeValueAsString(topUpEventResult);

        eventTopUpFailedListener.consume(message);

        verify(walletService).updateStatus(topUpEventResult, Status.FAIL);
    }

    @Test
    void consume_shouldDisposeMessageIntoDeadLetterQueueTopics() throws JsonProcessingException {
        TopUpEventResult topUpEventResult = TopUpEventResult.builder().eventId(UUID.randomUUID()).cif("000000001")
                .amount(10000).wallet("GO-PAY").destinationOfFund("00000000").reason("REASON").build();
        DeadLetterMessage<Disposable> deadLetter = DeadLetterMessage.builder().originTopics(originTopics)
                .message(topUpEventResult).build();
        String message = objectMapper.writeValueAsString(topUpEventResult);
        String deadLetterMessage = objectMapper.writeValueAsString(deadLetter);

        eventTopUpFailedListener.consume(message);

        verify(sagaOrchestrationService).orchestrate(deadLetterMessage, deadLetterTopics);
    }
}
