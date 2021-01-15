package com.personal.sagapattern.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.core.model.dto.TopUpRequest;
import com.personal.sagapattern.core.model.dto.TopUpResponse;
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
    private EventTopUpRepository eventTopUpRepository;

    @AfterEach
    void tearDown() {
        eventTopUpRepository.deleteAll();
    }

    @Test
    void topUp_shouldReturnStatusOkAndTopUpResponse_whenPostTransactionsIsInvoked() throws Exception {
        TopUpRequest topUpRequest = TopUpRequest.builder().cif("000000001").amount(10000).wallet("GO-PAY")
                .destinationOfFund("00000000").build();
        String topUpRequestJson = objectMapper.writeValueAsString(topUpRequest);
        RequestBuilder request = MockMvcRequestBuilders.post("/transactions").content(topUpRequestJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = client.perform(request).andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        TopUpResponse topUpResponse = objectMapper.readValue(result.getResponse().getContentAsString(),
                TopUpResponse.class);

        Assertions.assertEquals(topUpRequest.getCif(), topUpResponse.getCif());
        Assertions.assertEquals(topUpRequest.getAmount(), topUpResponse.getAmount());
        Assertions.assertEquals(topUpRequest.getWallet(), topUpResponse.getWallet());
    }
}
