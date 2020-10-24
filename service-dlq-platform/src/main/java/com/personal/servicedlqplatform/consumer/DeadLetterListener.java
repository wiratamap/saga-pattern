package com.personal.servicedlqplatform.consumer;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.servicedlqplatform.core.deadletter.DeadLetter;
import com.personal.servicedlqplatform.core.deadletter.DeadLetterService;
import com.personal.servicedlqplatform.core.deadletter.OriginTopic;
import com.personal.servicedlqplatform.core.deadletter.dto.DeadLetterDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
class DeadLetterListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DeadLetterService deadLetterService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "${event.top-up.dead-letter.topic}")
    void consume(@Payload String message) throws JsonProcessingException {
        DeadLetterDto deadLetterDto = objectMapper.readValue(message, DeadLetterDto.class);
        List<OriginTopic> originTopics = deadLetterDto.getOriginTopics().stream()
                .map(originTopic -> OriginTopic.builder().name(originTopic).build()).collect(Collectors.toList());
                
        DeadLetter deadLetter = DeadLetter.builder()
        .originalMessage(deadLetterDto.getOriginalMessage())
                .reason(deadLetterDto.getReason()).originTopics(originTopics).build();
        logger.info("Dead Letter detected, data: {}", deadLetter);
        logger.info("START ::: Processing Dead Letter");

        deadLetterService.create(deadLetter);

        logger.info("FINISH ::: Processing Dead Letter");
    }
}
