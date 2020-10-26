package com.personal.servicedlqplatform.core.deadletter;

import javax.persistence.Entity;

import com.personal.servicedlqplatform.common.model.AuditModel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class OriginTopic extends AuditModel {
    private String name;
}
