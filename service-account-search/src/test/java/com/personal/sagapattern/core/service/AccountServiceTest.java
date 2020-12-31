package com.personal.sagapattern.core.service;

import java.util.Optional;

import com.personal.sagapattern.core.AccountRepository;
import com.personal.sagapattern.core.AccountService;
import com.personal.sagapattern.core.model.Account;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void create_shouldSaveNewAccountData_whenCreateIsInvoked() {
        Account account = Account.builder().name("John Doe").cif("ACC1").build();

        accountService.create(account);

        Mockito.verify(accountRepository).save(account);
    }

    @Test
    void update_shouldUpdateExistingAccountData_whenAccountIsExist() {
        Account account = Account.builder().name("John Doe").cif("ACC1").build();
        Mockito.when(accountRepository.findByCif(account.getCif())).thenReturn(Optional.of(account));

        accountService.update(account);

        Mockito.verify(accountRepository).save(account);
    }

    @Test
    void update_shouldThrowExceptionAndNotSavingAccountData_whenAccountIsNotExist() {
        Account account = Account.builder().name("John Doe").cif("ACC1").build();
        Mockito.when(accountRepository.findByCif(account.getCif())).thenReturn(Optional.empty());

        accountService.update(account);

        Mockito.verify(accountRepository, Mockito.never()).save(account);
    }
}
