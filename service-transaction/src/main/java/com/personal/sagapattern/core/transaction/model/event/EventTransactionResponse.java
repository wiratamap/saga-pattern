package com.personal.sagapattern.core.transaction.model.event;

import java.util.UUID;

import com.personal.sagapattern.common.model.DataTransferAble;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventTransactionResponse implements DataTransferAble {
    private UUID eventId;
    private UUID id;
    private long amount;
    private String currency;
    private String note;
    private EventTransactionAccountInformation sourceAccountInformation;
    private EventTransactionAccountInformation destinationAccountInformation;
    private String failReason;
}
