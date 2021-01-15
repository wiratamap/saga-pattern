package com.personal.sagapattern.core.event_top_up.model.dto;

import java.util.UUID;

import com.personal.sagapattern.common.enumeration.Status;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
