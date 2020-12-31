package com.personal.sagapattern.core;

import java.util.Optional;
import java.util.UUID;

import com.personal.sagapattern.core.model.Account;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends ElasticsearchRepository<Account, UUID> {
    Optional<Account> findByCif(String cif);
}
