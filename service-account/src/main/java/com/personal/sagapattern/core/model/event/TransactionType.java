package com.personal.sagapattern.core.model.event;

import com.personal.sagapattern.core.model.Account;

public enum TransactionType {
    CREDIT {
        @Override
        public void updateBalance(Account account, EventTransactionRequest eventTransactionRequest) {
            long newBalance = account.getBalance() + eventTransactionRequest.getAmount();
            account.setBalance(newBalance);
        }
    },
    DEBIT {
        @Override
        public void updateBalance(Account account, EventTransactionRequest eventTransactionRequest) {
            long newBalance = account.getBalance() - eventTransactionRequest.getAmount();
            account.setBalance(newBalance);
        }
    };

    public abstract void updateBalance(Account account, EventTransactionRequest eventTransactionRequest);
}
