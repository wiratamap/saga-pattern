package com.personal.servicedlqplatform.core.deadletter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DeadLetterDeleteRequestDto {
    private DeadLetterActionRequestDto deleteAction;
}
