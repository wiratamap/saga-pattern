package com.personal.sagapattern.application_user;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.personal.sagapattern.common.model.AuditModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationUser extends AuditModel {
    @Column
    private String name;

    @Column
    private String cif;

    @Column
    private String email;

    @Column
    private String password;
}
