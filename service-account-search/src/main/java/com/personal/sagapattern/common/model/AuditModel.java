package com.personal.sagapattern.common.model;

import java.time.LocalDateTime;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Data;

@Data
public class AuditModel {
    @Field(type = FieldType.Date)
    private LocalDateTime createdDate;

    @Field(type = FieldType.Date)
    private LocalDateTime modifiedDate;
}
