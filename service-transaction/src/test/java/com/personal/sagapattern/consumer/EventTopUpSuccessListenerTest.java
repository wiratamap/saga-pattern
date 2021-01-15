package com.personal.sagapattern.consumer;

import static org.mockito.Mockito.verify;

import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.core.TransactionService;
import com.personal.sagapattern.core.model.Status;
import com.personal.sagapattern.core.model.dto.TopUpEventResult;

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
    private TransactionService walletService;

    @BeforeEach
    void setUp() {
        eventTopUpSuccessListener = new EventTopUpSuccessListener(walletService);
    }

    @Test
    void consume_shouldInvokeUpdateStatusWithTopUpRequestAndSuccessStatus() throws JsonProcessingException {
        TopUpEventResult topUpEventResult = TopUpEventResult.builder().eventId(UUID.randomUUID()).cif("000000001")
                .amount(10000).wallet("GO-PAY").destinationOfFund("00000000").reason("REASON").build();
        String message = objectMapper.writeValueAsString(topUpEventResult);

        eventTopUpSuccessListener.consume(message);

        verify(walletService).updateStatus(topUpEventResult, Status.SUCCESS);
    }

}
