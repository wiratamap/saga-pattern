package com.personal.sagapattern.core.transaction.model.dto;

import javax.validation.constraints.NotNull;

import com.personal.sagapattern.common.model.DataTransferAble;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DestinationAccountInformationDto implements DataTransferAble {
    private String accountHolderName;
    private String accountProvider;

    @NotNull(message = "destination externalAccountNumber cannot be null")
    private String externalAccountNumber;
}
