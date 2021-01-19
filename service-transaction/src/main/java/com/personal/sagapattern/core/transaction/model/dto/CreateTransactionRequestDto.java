package com.personal.sagapattern.core.transaction.model.dto;

import javax.validation.constraints.NotNull;

import com.personal.sagapattern.common.model.DataTransferAble;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTransactionRequestDto implements DataTransferAble {
    @NotNull(message = "sourceExternalAccountNumber cannot be null")
    private String sourceExternalAccountNumber;

    @NotNull(message = "destinationAccountInformation cannot be null")
    private DestinationAccountInformationDto destinationAccountInformation;

    @NotNull(message = "amount cannot be null")
    private long amount;

    @NotNull(message = "currency cannot be null")
    private String currency;

    private String note;
}
