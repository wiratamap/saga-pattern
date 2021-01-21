package com.personal.sagapattern.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.core.AccountService;
import com.personal.sagapattern.core.model.event.EventTransactionRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventTransactionListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AccountService accountService;

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${event.transaction.topic}")
    void consume(@Payload String message) throws JsonProcessingException {
        EventTransactionRequest eventTransactionRequest = objectMapper.readValue(message,
                EventTransactionRequest.class);
        logger.info("Event detected, data: {}", eventTransactionRequest);
        logger.info("START ::: Processing Event");

        // accountService.topUp(eventTransactionRequest);

        logger.info("FINISH ::: Processing Event");
    }
}
