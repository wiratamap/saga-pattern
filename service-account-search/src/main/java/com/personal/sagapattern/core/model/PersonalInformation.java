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
public class PersonalInformation extends AuditModel {
    private LocalDateTime dateOfBirth;
    private String placeOfBirth;
    private String identityNumber;
    private String religion;
    private String nationality;
    private MaritalStatus maritalStatus;
    private String npwpNumber;
    private String identityImagePath;
    private String npwpImagePath;
    private String selfieImagePath;
}
