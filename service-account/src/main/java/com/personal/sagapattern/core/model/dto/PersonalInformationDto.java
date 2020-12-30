package com.personal.sagapattern.core.model.dto;

import java.time.LocalDateTime;

import com.personal.sagapattern.core.model.MaritalStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PersonalInformationDto {
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
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