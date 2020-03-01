package com.personal.sagapattern.core.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopUpEventResult {
    private UUID eventId;
    private String reason;
    private String cif;
    private String wallet;
    private String destinationOfFund;
    private int amount;
}
