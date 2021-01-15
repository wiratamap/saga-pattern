package com.personal.sagapattern.core.transaction.model.dto;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DestinationAccountInformationDto {
    private String accountHolderName;
    private String accountProvider;

    @NotNull(message = "destination externalAccountNumber cannot be null")
    private String externalAccountNumber;
}
