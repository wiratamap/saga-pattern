package com.personal.sagapattern.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.common.enumeration.Status;
import com.personal.sagapattern.core.transaction.TransactionService;
import com.personal.sagapattern.core.transaction.model.event.EventTransactionResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventTransactionSuccessListener {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TransactionService transactionService;

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${event.success-transaction.topics}")
    void consume(@Payload String message) throws JsonProcessingException {
        EventTransactionResponse transactionEventResult = objectMapper.readValue(message,
                EventTransactionResponse.class);
        logger.info("Event response detected, data: {}", transactionEventResult);
        logger.info("START ::: Processing event response");

        transactionService.updateStatus(transactionEventResult, Status.SUCCESS);

        logger.info("FINISH ::: Processing event response");
    }
}
