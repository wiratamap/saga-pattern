package com.personal.sagapattern.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;

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
public class Account extends AuditModel implements DataTransferAble {
    @Column
    private String name;

    @Column
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column
    private String email;

    @Column
    private String phoneNumber;

    @Column
    private String cif;

    @Column
    private long balance;

    @Column
    private boolean isPriority;

    @OneToOne(mappedBy = "account")
    private AccountDetail accountDetail;

    @OneToOne(mappedBy = "account")
    private PersonalInformation personalInformation;
}
