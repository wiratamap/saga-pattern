package com.personal.sagapattern.core.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.core.transaction.model.dto.CreateTransactionRequestDto;
import com.personal.sagapattern.core.transaction.model.dto.CreateTransactionResponseDto;
import com.personal.sagapattern.core.transaction.model.dto.DestinationAccountInformationDto;
import com.personal.sagapattern.orchestration.service.SagaOrchestrationService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@MockBean(SagaOrchestrationService.class)
class TransactionControllerTest {
    @Autowired
    private MockMvc client;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransactionRepository transactionRepository;

    @AfterEach
    void tearDown() {
        transactionRepository.deleteAll();
    }

    @Test
    void create_shouldReturnStatusCreatedAndCreateTransactionResponse_whenPostTransactionsIsInvoked() throws Exception {
        DestinationAccountInformationDto destinationAccountInformationDto = DestinationAccountInformationDto.builder()
                .accountHolderName("Bertha Doe").accountProvider("GO-PAY").externalAccountNumber("987654321").build();
        CreateTransactionRequestDto createTransactionRequestDto = CreateTransactionRequestDto.builder()
                .sourceExternalAccountNumber("123456789")
                .destinationAccountInformation(destinationAccountInformationDto).amount(100_000).currency("IDR")
                .build();
        String createTransactionRequestJson = objectMapper.writeValueAsString(createTransactionRequestDto);
        RequestBuilder request = MockMvcRequestBuilders.post("/transactions").content(createTransactionRequestJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = client.perform(request).andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();
        CreateTransactionResponseDto createTransactionResponse = objectMapper
                .readValue(result.getResponse().getContentAsString(), CreateTransactionResponseDto.class);

        Assertions.assertEquals(createTransactionRequestDto.getCurrency(), createTransactionResponse.getCurrency());
        Assertions.assertEquals(createTransactionRequestDto.getAmount(), createTransactionResponse.getAmount());
    }
}
