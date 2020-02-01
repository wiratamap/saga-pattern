package com.personal.sagapattern.core.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopUpRequest {
    @NotNull(message = "CIF cannot be null")
    private String cif;

    @NotNull(message = "wallet cannot be null")
    private String wallet;

    @NotNull(message = "amount cannot be null")
    private int amount;
}
