package com.personal.sagapattern.core.model;

import java.util.UUID;

import com.personal.sagapattern.common.model.AuditModel;
import com.personal.sagapattern.common.model.DataTransferAble;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Document(indexName = "account", type = "_doc")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account extends AuditModel implements DataTransferAble {
    @Id
    private UUID id;
    private String name;
    private Gender gender;
    private String email;
    private String phoneNumber;
    private String cif;
    private long balance;
    private boolean isPriority;

    @Field(type = FieldType.Nested, includeInParent = true)
    private AccountDetail accountDetail;

    @Field(type = FieldType.Nested, includeInParent = true)
    private PersonalInformation personalInformation;
}
