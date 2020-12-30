package com.personal.sagapattern.core.model.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountDetailDto {
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String externalAccountNumber;
    private String cardHolderName;
    private String cardProvider;
    private String cardNumber;
    private LocalDateTime cardExpirationDate;
    private LocalDateTime cardCreationDate;
    private int cvvCode;
}
