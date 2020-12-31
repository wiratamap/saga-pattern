package com.personal.sagapattern.core;

import com.personal.sagapattern.core.model.Account;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    public void create(Account account) {
        this.accountRepository.save(account);
    }
}
