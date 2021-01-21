package com.personal.sagapattern.consumer;

import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.core.AccountService;
import com.personal.sagapattern.core.model.event.EventTransactionAccountInformation;
import com.personal.sagapattern.core.model.event.EventTransactionRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EventTransactionListenerTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    private EventTransactionListener transactionEventListener;

    @Mock
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        transactionEventListener = new EventTransactionListener(accountService, objectMapper);
    }

    @Test
    void consume_shouldInvokeTopUpWithTopUpRequest() throws JsonProcessingException {
        EventTransactionAccountInformation sourceAccountInformation = EventTransactionAccountInformation.builder()
                .accountProvider("MeBank").externalAccountNumber("0000").build();
        EventTransactionAccountInformation destinationAccountInformation = EventTransactionAccountInformation.builder()
                .accountProvider("GO-PAY").externalAccountNumber("0001").build();
        EventTransactionRequest eventTransactionRequest = EventTransactionRequest.builder().amount(150_000)
                .currency("IDR").sourceAccountInformation(sourceAccountInformation)
                .destinationAccountInformation(destinationAccountInformation).build();
        String message = objectMapper.writeValueAsString(eventTransactionRequest);

        this.transactionEventListener.consume(message);

        verify(accountService).processTransaction(eventTransactionRequest);
    }
}
