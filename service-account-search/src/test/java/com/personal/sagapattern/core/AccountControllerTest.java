package com.personal.sagapattern.core;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.common.elasticsearch_query.dto.FilterOperator;
import com.personal.sagapattern.common.elasticsearch_query.dto.FilterRequest;
import com.personal.sagapattern.core.model.Account;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(controllers = AccountController.class)
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc client;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void filter_shouldReturnAvailableAccounts_whenAccountsIsMatchWithFilterRequest() throws Exception {
        Account account = Account.builder().name("John Doe").cif("ACC1").build();
        List<Account> accounts = Collections.singletonList(account);
        int pageRequest = 0;
        int pageSizeRequest = 10;
        FilterRequest filterRequest = FilterRequest.builder().key("cif").operator(FilterOperator.EQUAL).value("cif0001")
                .build();
        Mockito.when(this.accountService.filter(pageRequest, pageSizeRequest, Collections.singletonList(filterRequest)))
                .thenReturn(accounts);
        String filterRequestBody = this.objectMapper.writeValueAsString(Collections.singletonList(filterRequest));
        RequestBuilder request = MockMvcRequestBuilders.get("/accounts").param("page", String.valueOf(pageRequest))
                .param("size", String.valueOf(pageSizeRequest)).content(filterRequestBody)
                .contentType(MediaType.APPLICATION_JSON);

        this.client.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].cif", Matchers.equalTo(account.getCif())));
    }
}
