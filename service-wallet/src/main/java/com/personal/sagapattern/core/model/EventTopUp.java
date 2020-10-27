package com.personal.sagapattern.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.personal.librarycommonmodel.AuditModel;
import com.personal.sagapattern.core.enumeration.Status;

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
public class EventTopUp extends AuditModel {
    @Column
    private String cif;

    @Column
    private int amount;

    @Column
    private String wallet;

    @Column
    private String destinationOfFund;

    @Column
    private String reason;

    @Column
    @Enumerated(value = EnumType.STRING)
    private Status status;
}
