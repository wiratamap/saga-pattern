package com.personal.sagapattern.consumer;

import static org.mockito.Mockito.verify;

import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.core.event_top_up.model.Status;
import com.personal.sagapattern.core.event_top_up.model.dto.TopUpEventResult;
import com.personal.sagapattern.core.transaction.TransactionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventTopUpSuccessListenerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private EventTopUpSuccessListener eventTopUpSuccessListener;

    @Mock
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        eventTopUpSuccessListener = new EventTopUpSuccessListener(transactionService);
    }

    @Test
    void consume_shouldInvokeUpdateStatusWithTopUpRequestAndSuccessStatus() throws JsonProcessingException {
        TopUpEventResult topUpEventResult = TopUpEventResult.builder().eventId(UUID.randomUUID()).cif("000000001")
                .amount(10000).wallet("GO-PAY").destinationOfFund("00000000").reason("REASON").build();
        String message = objectMapper.writeValueAsString(topUpEventResult);

        eventTopUpSuccessListener.consume(message);

        verify(transactionService).updateStatus(topUpEventResult, Status.SUCCESS);
    }

}
