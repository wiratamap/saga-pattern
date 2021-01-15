package com.personal.sagapattern.core.event_top_up.model.dto;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import com.personal.sagapattern.common.model.Disposable;

import org.modelmapper.ModelMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopUpRequest implements Disposable {
    @NotNull(message = "CIF cannot be null")
    private String cif;

    @NotNull(message = "wallet cannot be null")
    private String wallet;

    @NotNull(message = "destination of fund cannot be null")
    private String destinationOfFund;

    @NotNull(message = "amount cannot be null")
    private int amount;

    private UUID eventId;

    public static TopUpRequest convertFrom(Object object) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(object, TopUpRequest.class);
    }
}
