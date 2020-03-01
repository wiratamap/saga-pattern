package com.personal.sagapattern.core.model;

import com.personal.sagapattern.common.model.AuditModel;
import com.personal.sagapattern.core.enumeration.Status;
import lombok.*;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(schema = "wallet")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventTopUp extends AuditModel {
    @Column
    private String cif;

    @Column
    private int amount;

    @Column
    private String wallet;

    @Column
    private String destinationOfFund;

    @Column
    private String reason;

    @Column
    @Enumerated(value = EnumType.STRING)
    private Status status;
}
