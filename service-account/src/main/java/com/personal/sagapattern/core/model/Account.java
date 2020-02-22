package com.personal.sagapattern.core.model;

import com.personal.sagapattern.common.model.AuditModel;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(schema = "account")
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
