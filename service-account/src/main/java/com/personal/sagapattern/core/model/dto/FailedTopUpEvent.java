package com.personal.sagapattern.core.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FailedTopUpEvent {
    private String cif;
    private String wallet;
    private String destinationOfFund;
    private int amount;
    private String reason;
}
