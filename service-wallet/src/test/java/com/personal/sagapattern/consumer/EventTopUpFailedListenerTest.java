package com.personal.sagapattern.consumer;

import static org.mockito.Mockito.verify;

import java.util.UUID;

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

@ExtendWith(MockitoExtension.class)
class EventTopUpFailedListenerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private EventTopUpFailedListener eventTopUpFailedListener;

    @Mock
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        eventTopUpFailedListener = new EventTopUpFailedListener(walletService);
    }

    @Test
    void consume_shouldInvokeUpdateStatusWithTopUpRequestAndFailedStatus() throws JsonProcessingException {
        TopUpEventResult topUpEventResult = TopUpEventResult.builder()
                .eventId(UUID.randomUUID())
                .cif("000000001")
                .amount(10000)
                .wallet("GO-PAY")
                .destinationOfFund("00000000")
                .reason("REASON")
                .build();
        String message = objectMapper.writeValueAsString(topUpEventResult);

        eventTopUpFailedListener.consume(message);

        verify(walletService).updateStatus(topUpEventResult, Status.FAIL);
    }
}
