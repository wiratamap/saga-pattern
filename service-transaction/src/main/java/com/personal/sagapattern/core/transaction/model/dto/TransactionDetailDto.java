package com.personal.sagapattern.core.transaction.model.dto;

import com.personal.sagapattern.core.transaction.model.TransactionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDetailDto {
    private String accountHolderName;
    private String externalAccountNumber;
    private String accountProvider;
    private TransactionType transactionType;
}
