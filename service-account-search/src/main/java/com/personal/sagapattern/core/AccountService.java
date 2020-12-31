package com.personal.sagapattern.core;

import java.util.Optional;

import com.personal.sagapattern.core.model.Account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void create(Account account) {
        this.accountRepository.save(account);
    }

    public void update(Account account) {
        Optional<Account> existingAccount = this.accountRepository.findByCif(account.getCif());

        if (!existingAccount.isPresent()) {
            this.logger.info("Account with CIF: {} not found", account.getCif());
            return;
        }
        this.accountRepository.save(account);
    }
}
