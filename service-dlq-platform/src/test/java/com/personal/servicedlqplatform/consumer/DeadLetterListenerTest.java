package com.personal.servicedlqplatform.consumer;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.servicedlqplatform.core.deadletter.DeadLetter;
import com.personal.servicedlqplatform.core.deadletter.DeadLetterService;
import com.personal.servicedlqplatform.core.deadletter.OriginTopic;
import com.personal.servicedlqplatform.core.deadletter.dto.DeadLetterDto;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeadLetterListenerTest {

    @Mock
    private DeadLetterService deadLetterService;

    @InjectMocks
    private DeadLetterListener deadLetterListener;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void consume_shouldCreateNewDeadLetterMessageFromDeadLetterTopic() throws JsonProcessingException {
        List<String> originTopics = Collections.singletonList("ORIGINAL_TOPIC");
        DeadLetterDto deadLetterDto = DeadLetterDto.builder().originalMessage("message").originTopics(originTopics)
                .reason("fail reason").build();
        String deadLetterMessage = objectMapper.writeValueAsString(deadLetterDto);
        List<OriginTopic> expectedOriginTopics = deadLetterDto.getOriginTopics().stream()
                .map(originTopic -> OriginTopic.builder().name(originTopic).build()).collect(Collectors.toList());
        DeadLetter expectedDeadLetter = DeadLetter.builder().originalMessage(deadLetterDto.getOriginalMessage())
                .originTopics(expectedOriginTopics).reason(deadLetterDto.getReason()).build();

        deadLetterListener.consume(deadLetterMessage);

        Mockito.verify(deadLetterService, Mockito.atMostOnce()).create(expectedDeadLetter);
    }
}
