package com.personal.sagapattern.consumer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.common.enumeration.Status;
import com.personal.sagapattern.common.model.DeadLetterMessage;
import com.personal.sagapattern.common.model.Disposable;
import com.personal.sagapattern.core.transaction.TransactionService;
import com.personal.sagapattern.core.transaction.model.event.EventTransactionRequest;
import com.personal.sagapattern.core.transaction.model.event.EventTransactionResponse;
import com.personal.sagapattern.orchestration.service.SagaOrchestrationService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventTransactionFailedListenerTest {
    @Mock
    private TransactionService transactionService;

    @Mock
	private SagaOrchestrationService sagaOrchestrationService;

    @Spy
    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private EventTransactionFailedListener eventTransactionFailedListener;

    private final List<String> deadLetterTopics = Collections.singletonList("DEAD_LETTER_QUEUE");
    private final List<String> originTopics = Arrays.asList("EVENT_TRANSACTION_REQUEST", "SURROUNDING_NOTIFICATION",
			"TRANSFER_NOTIFICATION");

    @Test
    void consume_shouldInvokeUpdateStatusWithTopUpRequestAndFailedStatusAndDisposeMessageIntoDeadLetterQueueTopics() throws JsonProcessingException {
        EventTransactionResponse eventTransactionResponse = EventTransactionResponse.builder().amount(100_000)
                .currency("IDR").eventId(UUID.randomUUID()).failReason("FAILED CLAUSE").build();
        EventTransactionRequest originalMessage = eventTransactionResponse.convertTo(EventTransactionRequest.class);
        DeadLetterMessage<Disposable> deadLetter = DeadLetterMessage.builder().originTopics(originTopics)
                .originalMessage(originalMessage).reason(eventTransactionResponse.getFailReason()).build();
        String message = objectMapper.writeValueAsString(eventTransactionResponse);
        String deadLetterMessage = objectMapper.writeValueAsString(deadLetter);

        this.eventTransactionFailedListener.consume(message);

        Mockito.verify(this.transactionService).updateStatus(eventTransactionResponse, Status.FAIL);
        Mockito.verify(this.sagaOrchestrationService).orchestrate(deadLetterMessage, deadLetterTopics);
    }
}
