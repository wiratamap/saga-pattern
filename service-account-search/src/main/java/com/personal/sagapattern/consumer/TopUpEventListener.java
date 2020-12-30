package com.personal.sagapattern.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.core.AccountService;
import com.personal.sagapattern.core.model.dto.TopUpRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TopUpEventListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AccountService accountService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "${event.top-up.topic}")
    void consume(@Payload String message) throws JsonProcessingException {
        TopUpRequest topUpRequest = objectMapper.readValue(message, TopUpRequest.class);
        logger.info("Event detected, data: {}", topUpRequest);
        logger.info("START ::: Processing Event");

        accountService.topUp(topUpRequest);

        logger.info("FINISH ::: Processing Event");
    }
}
