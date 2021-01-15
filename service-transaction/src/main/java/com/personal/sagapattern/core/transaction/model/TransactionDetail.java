package com.personal.sagapattern.core.transaction.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.personal.sagapattern.common.model.AuditModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDetail extends AuditModel {
    @Column
    private String accountHolderName;

    @Column
    private String externalAccountNumber;

    @Column
    private String accountProvider;

    @Column
    private TransactionType transactionType;
}
