package com.personal.sagapattern.core.transaction.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.personal.sagapattern.common.enumeration.Status;
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
public class Transaction extends AuditModel {
    @Column
    private long amount;

    @Column
    private String currency;

    @Column
    private String note;

    @Column
    @Enumerated(value = EnumType.STRING)
    private Status status;

    @Column
    private String failReason;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "transaction_id")
    private List<TransactionDetail> transactionDetails;
}
