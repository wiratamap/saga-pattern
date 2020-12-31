package com.personal.sagapattern.consumer;

import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.core.AccountService;
import com.personal.sagapattern.core.model.Account;
import com.personal.sagapattern.core.model.Gender;
import com.personal.sagapattern.core.model.dto.AccountDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountChangedListenerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private AccountChangedListener accountChangedListener;

    @Mock
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountChangedListener = new AccountChangedListener(accountService, objectMapper);
    }

    @Test
    void consume_shouldInvokeCreateWithAccount() throws JsonProcessingException {
        AccountDto accountDto = AccountDto.builder().cif("00000001").balance(10000).gender(Gender.MALE).build();
        String message = objectMapper.writeValueAsString(accountDto);
        Account account = accountDto.convertTo(Account.class);

        accountChangedListener.consume(message);

        verify(accountService).create(account);
    }
}
