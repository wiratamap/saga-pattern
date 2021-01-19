package com.personal.sagapattern.core.transaction;

import java.util.UUID;

import com.personal.sagapattern.core.transaction.model.Transaction;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    
}
