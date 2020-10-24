package com.personal.sagapattern.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.personal.sagapattern.common.model.AuditModel;

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
public class Account extends AuditModel {
    @Column
    private String cif;

    @Column
    private int balance;
}
