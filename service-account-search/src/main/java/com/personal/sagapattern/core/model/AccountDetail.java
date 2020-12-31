package com.personal.sagapattern.core.model;

import java.time.LocalDateTime;

import com.personal.sagapattern.common.model.AuditModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDetail extends AuditModel {
    private String externalAccountNumber;
    private String cardHolderName;
    private String cardProvider;
    private String cardNumber;
    private LocalDateTime cardExpirationDate;
    private LocalDateTime cardCreationDate;
    private int cvvCode;
}
