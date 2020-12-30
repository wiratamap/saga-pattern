package com.personal.sagapattern.core.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.personal.sagapattern.core.model.Gender;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountDto {
    private UUID id;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String name;
    private Gender gender;
    private String email;
    private String phoneNumber;
    private String cif;
    private long balance;
    private boolean isPriority;
    private AccountDetailDto accountDetail;
    private PersonalInformationDto personalInformation;
}
