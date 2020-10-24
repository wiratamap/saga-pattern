package com.personal.servicedlqplatform.core.deadletter.dto;

import java.util.List;
import java.util.UUID;

import com.personal.servicedlqplatform.core.deadletter.DeadLetter;

import org.modelmapper.ModelMapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DeadLetterResponseDto {
    private UUID id;
    private String originalMessage;
    private String reason;
    private List<OriginTopicResponseDto> originTopics;

    public static DeadLetterResponseDto convertFromEntity(DeadLetter deadLetter) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(deadLetter, DeadLetterResponseDto.class);
    }
}
