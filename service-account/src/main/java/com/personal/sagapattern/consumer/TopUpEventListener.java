package com.personal.sagapattern.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.personal.sagapattern.core.model.dto.TopUpRequest;
import com.personal.sagapattern.core.service.AccountService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TopUpEventListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${event.top-up.topic}")
    private String eventTopUpTopic;

    private final AccountService accountService;

    @KafkaListener(topics = "${event.top-up.topic}")
    void consume(@Payload TopUpRequest topUpRequest) throws JsonProcessingException {
        logger.info("Event detected from {}, data: {}", eventTopUpTopic, topUpRequest);
        logger.info("START ::: Processing Event");

        accountService.topUp(topUpRequest);

        logger.info("FINISH ::: Processing Event");
    }
}
