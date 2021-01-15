package com.personal.sagapattern.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.common.enumeration.Status;
import com.personal.sagapattern.core.event_top_up.model.dto.TopUpEventResult;
import com.personal.sagapattern.core.transaction.TransactionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EventTopUpSuccessListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TransactionService walletService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "${event.success-top-up.topics}")
    void consume(@Payload String message) throws JsonProcessingException {
        TopUpEventResult topUpEventResult = objectMapper.readValue(message, TopUpEventResult.class);
        logger.info("Event response detected, data: {}", topUpEventResult);
        logger.info("START ::: Processing event response");

        walletService.updateStatus(topUpEventResult, Status.SUCCESS);

        logger.info("FINISH ::: Processing event response");
    }
}
