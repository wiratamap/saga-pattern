package com.personal.sagapattern.core.transaction.model.dto;

import java.util.List;

import com.personal.sagapattern.common.enumeration.Status;
import com.personal.sagapattern.common.model.DataTransferAble;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTransactionResponseDto implements DataTransferAble {
    private long amount;
    private String currency;
    private String note;
    private Status status;
    private String failReason;
    private List<TransactionDetailDto> transactionDetails;
}
