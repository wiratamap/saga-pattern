package com.personal.sagapattern.core.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopUpRequest {
    @NotNull(message = "CIF cannot be null")
    private String cif;

    @NotNull(message = "wallet cannot be null")
    private String wallet;

    @NotNull(message = "destination of fund cannot be null")
    private String destinationOfFund;

    @NotNull(message = "amount cannot be null")
    private int amount;

    private UUID eventId;
}
