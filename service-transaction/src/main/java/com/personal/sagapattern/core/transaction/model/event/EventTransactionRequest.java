package com.personal.sagapattern.core.transaction.model.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventTransactionRequest {
    private long amount;
    private String currency;
    private String note;
    private EventTransactionAccountInformation sourceAccountInformation;
    private EventTransactionAccountInformation destinationAccountInformation;
}
