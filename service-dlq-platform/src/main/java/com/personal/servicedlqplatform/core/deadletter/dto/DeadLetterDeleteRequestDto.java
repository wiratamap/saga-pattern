package com.personal.servicedlqplatform.core.deadletter.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DeadLetterDeleteRequestDto {
    private DeadLetterActionRequestDto deleteAction;
}
