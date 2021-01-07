package com.personal.sagapattern.core;

import java.util.List;
import java.util.stream.Collectors;

import com.personal.sagapattern.common.elasticsearch_query.dto.FilterRequest;
import com.personal.sagapattern.core.model.Account;
import com.personal.sagapattern.core.model.dto.AccountDto;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountDto>> filter(@RequestParam int page, @RequestParam int size,
            @RequestBody List<FilterRequest> filterRequests) {
        List<Account> filteredAccounts = this.accountService.filter(page, size, filterRequests);
        List<AccountDto> accounts = filteredAccounts.stream()
                .map(account -> account.convertTo(AccountDto.class)).collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(accounts);
    }
}
