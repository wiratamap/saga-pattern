package com.personal.sagapattern.core.model.dto;

import com.personal.sagapattern.core.enumeration.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopUpResponse {
    private UUID eventId;
    private String cif;
    private String wallet;
    private String destinationOfFund;
    private int amount;
    private Status status;
}
