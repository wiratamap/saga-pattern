package com.personal.sagapattern.core.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class PersonalInformation extends AuditModel {
    @Column
    private LocalDateTime dateOfBirth;

    @Column
    private String placeOfBirth;

    @Column
    private String identityNumber;

    @Column
    private String religion;

    @Column
    private String nationality;

    @Column
    @Enumerated(EnumType.STRING)
    private MaritalStatus maritalStatus;

    @Column
    private String npwpNumber;

    @Column
    private String identityImagePath;

    @Column
    private String npwpImagePath;

    @Column
    private String selfieImagePath;

    @OneToOne
    @JoinColumn(name = "account_id")
    @JsonIgnore
    private Account account;
}
