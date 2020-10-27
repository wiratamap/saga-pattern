package com.personal.sagapattern.core.model.dto;

import java.util.UUID;

import com.personal.sagapattern.contract.Disposable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopUpEventResult implements Disposable {
    private UUID eventId;
    private String reason;
    private String cif;
    private String wallet;
    private String destinationOfFund;
    private int amount;
}
