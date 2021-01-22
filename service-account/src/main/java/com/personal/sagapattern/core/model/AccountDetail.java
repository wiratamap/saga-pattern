package com.personal.sagapattern.core.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.personal.sagapattern.common.model.AuditModel;
import com.personal.sagapattern.common.model.DataTransferAble;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDetail extends AuditModel implements DataTransferAble {
    @Column
    private String externalAccountNumber;

    @Column
    private String cardHolderName;

    @Column
    private String cardProvider;

    @Column
    private String cardNumber;

    @Column
    private LocalDateTime cardExpirationDate;

    @Column
    private LocalDateTime cardCreationDate;

    @Column
    private int cvvCode;

    @OneToOne
    @JoinColumn(name = "account_id")
    @JsonIgnore
    private Account account;
}
