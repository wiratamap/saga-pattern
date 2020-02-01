package com.personal.sagapattern.core.model.dto;

import com.personal.sagapattern.core.enumeration.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopUpResponse {
    private String cif;
    private String wallet;
    private int amount;
    private Status status;
}
