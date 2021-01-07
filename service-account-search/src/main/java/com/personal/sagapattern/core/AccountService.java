package com.personal.sagapattern.core;

import java.util.List;
import java.util.Optional;

import com.personal.sagapattern.common.elasticsearch_query.dto.FilterRequest;
import com.personal.sagapattern.core.model.Account;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    public List<Account> filter(int page, int size, List<FilterRequest> filterRequests) {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        filterRequests.forEach(filterRequest -> filterRequest.getOperator().query(filterRequest, query));

        Page<Account> searchedAccounts = this.accountRepository.search(query, PageRequest.of(page, size));
        return searchedAccounts.getContent();
    }
}
