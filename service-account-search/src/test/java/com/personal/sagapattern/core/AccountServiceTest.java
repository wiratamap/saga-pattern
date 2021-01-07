package com.personal.sagapattern.core;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.personal.sagapattern.common.elasticsearch_query.dto.FilterOperator;
import com.personal.sagapattern.common.elasticsearch_query.dto.FilterRequest;
import com.personal.sagapattern.core.model.Account;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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

    @Test
    void filter_shouldReturnFilteredAccount_whenInvokedWithDefinedPageSizeAndFilterRequests() {
        Account account = Account.builder().cif("CIF0001").balance(100_000).build();
        int pageRequest = 0;
        int pageSizeRequest = 10;
        Mockito.when(accountRepository.search(Mockito.any(BoolQueryBuilder.class),
                Mockito.eq(PageRequest.of(pageRequest, pageSizeRequest))))
                .thenReturn(new PageImpl<>(Collections.singletonList(account)));
        FilterRequest filterRequest = FilterRequest.builder().key("cif").operator(FilterOperator.EQUAL).value("cif0001")
                .build();
        BoolQueryBuilder expectedQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery(filterRequest.getKey(), filterRequest.getValue()));

        List<Account> actualAccounts = accountService.filter(pageRequest, pageSizeRequest,
                Collections.singletonList(filterRequest));

        Mockito.verify(accountRepository).search(expectedQuery, PageRequest.of(pageRequest, pageSizeRequest));
        Assertions.assertEquals(Collections.singletonList(account), actualAccounts);
    }
}
