package com.personal.sagapattern.consumer;

import static org.mockito.Mockito.verify;

import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.common.enumeration.Status;
import com.personal.sagapattern.core.transaction.TransactionService;
import com.personal.sagapattern.core.transaction.model.event.EventTransactionResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventTransactionSuccessListenerTest {
    @Mock
    private TransactionService transactionService;

    @Spy
    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private EventTransactionSuccessListener eventTransactionSuccessListener;

    @Test
    void consume_shouldInvokeUpdateStatusWithTopUpRequestAndSuccessStatus() throws JsonProcessingException {
        EventTransactionResponse eventTransactionResponse = EventTransactionResponse.builder().amount(100_000)
                .currency("IDR").eventId(UUID.randomUUID()).build();
        String message = objectMapper.writeValueAsString(eventTransactionResponse);

        eventTransactionSuccessListener.consume(message);

        verify(transactionService).updateStatus(eventTransactionResponse, Status.SUCCESS);
    }
}
