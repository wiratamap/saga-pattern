package com.personal.servicedlqplatform.consumer;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.servicedlqplatform.core.deadletter.DeadLetter;
import com.personal.servicedlqplatform.core.deadletter.DeadLetterService;
import com.personal.servicedlqplatform.core.deadletter.OriginalTopic;

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
        OriginalTopic originalTopic = OriginalTopic.builder().name("ORIGINAL_TOPIC").build();
        List<OriginalTopic> originalTopics = Collections.singletonList(originalTopic);
        DeadLetter deadLetter = DeadLetter.builder().message("something").reason("fail reason")
                .originalTopics(originalTopics).build();
        String deadLetterMessage = objectMapper.writeValueAsString(deadLetter);

        deadLetterListener.consume(deadLetterMessage);

        Mockito.verify(deadLetterService, Mockito.atMostOnce()).create(deadLetter);
    }
    
}
