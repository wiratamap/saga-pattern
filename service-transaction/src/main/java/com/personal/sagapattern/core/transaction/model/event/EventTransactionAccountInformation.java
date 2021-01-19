package com.personal.sagapattern.core.transaction.model.event;

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
}
