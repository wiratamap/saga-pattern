package com.personal.sagapattern.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.core.enumeration.Status;
import com.personal.sagapattern.core.model.dto.TopUpEventResult;
import com.personal.sagapattern.core.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventTopUpSuccessListenerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private EventTopUpSuccessListener eventTopUpSuccessListener;

    @Mock
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        eventTopUpSuccessListener = new EventTopUpSuccessListener(walletService);
    }

    @Test
    void consume_shouldInvokeUpdateStatusWithTopUpRequestAndSuccessStatus() throws JsonProcessingException {
        TopUpEventResult topUpEventResult = TopUpEventResult.builder()
                .eventId(UUID.randomUUID())
                .cif("000000001")
                .amount(10000)
                .wallet("GO-PAY")
                .destinationOfFund("00000000")
                .reason("REASON")
                .build();
        String message = objectMapper.writeValueAsString(topUpEventResult);

        eventTopUpSuccessListener.consume(message);

        verify(walletService).updateStatus(topUpEventResult, Status.SUCCESS);
    }

}
