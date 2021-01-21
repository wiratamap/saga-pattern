package com.personal.sagapattern.core.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventTransactionAccountInformation {
    private String accountHolderName;
    private String externalAccountNumber;
    private String accountProvider;
    private TransactionType transactionType;
}
