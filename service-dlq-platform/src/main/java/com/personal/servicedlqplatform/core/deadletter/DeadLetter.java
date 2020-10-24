package com.personal.servicedlqplatform.core.deadletter;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

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
public class DeadLetter extends AuditModel {
    @Column
    private String message;

    @Column
    private String reason;

    @OneToMany(orphanRemoval = true)
    @JoinColumn(name = "dead_letter_id")
    private List<OriginalTopic> originalTopics;
}
