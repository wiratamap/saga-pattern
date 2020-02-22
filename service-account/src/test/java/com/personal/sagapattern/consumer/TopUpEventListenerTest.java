package com.personal.sagapattern.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.personal.sagapattern.core.model.dto.TopUpRequest;
import com.personal.sagapattern.core.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TopUpEventListenerTest {

    private TopUpEventListener topUpEventListener;

    @Mock
    private AccountService accountService;

    private String eventTopUpTopic = "EVENT_TOP_UP_REQUEST";

    @BeforeEach
    void setUp() {
        topUpEventListener = new TopUpEventListener(eventTopUpTopic, accountService);
    }

    @Test
    void consume_shouldInvokeTopUpWithTopUpRequest() throws JsonProcessingException {
        TopUpRequest topUpRequest = TopUpRequest.builder()
                .cif("00000001")
                .amount(10000)
                .wallet("GO-PAY")
                .destinationOfFund("00000000")
                .build();

        topUpEventListener.consume(topUpRequest);

        verify(accountService).topUp(topUpRequest);
    }
}
