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
public class TopUpEventResponse {
    private UUID eventId;
    private String cif;
    private String wallet;
    private String destinationOfFund;
    private int amount;
    private String reason;
}
