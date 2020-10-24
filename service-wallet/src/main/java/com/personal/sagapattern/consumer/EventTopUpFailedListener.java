package com.personal.sagapattern.consumer;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.common.model.Disposable;
import com.personal.sagapattern.core.enumeration.Status;
import com.personal.sagapattern.core.model.dto.DeadLetterMessage;
import com.personal.sagapattern.core.model.dto.TopUpEventResult;
import com.personal.sagapattern.core.model.dto.TopUpRequest;
import com.personal.sagapattern.core.service.WalletService;
import com.personal.sagapattern.orchestration.service.SagaOrchestrationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EventTopUpFailedListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final WalletService walletService;

    private final SagaOrchestrationService sagaOrchestrationService;

    @Value("${event.top-up.dead-letter.topics}")
    private final List<String> deadLetterTopics;

    @Value("${event.top-up.topics}")
    private final List<String> originTopics;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "${event.failed-top-up.topics}")
    void consume(@Payload String message) throws JsonProcessingException {
        TopUpEventResult topUpEventResult = objectMapper.readValue(message, TopUpEventResult.class);
        logger.info("Event response detected, data: {}", topUpEventResult);
        logger.info("START ::: Processing event response");

        walletService.updateStatus(topUpEventResult, Status.FAIL);

        TopUpRequest originalMessage = TopUpRequest.convertFrom(topUpEventResult);
        DeadLetterMessage<Disposable> deadLetter = DeadLetterMessage.builder().originTopics(originTopics)
                .originalMessage(originalMessage).reason(topUpEventResult.getReason()).build();
        String deadLetterMessage = objectMapper.writeValueAsString(deadLetter);

        sagaOrchestrationService.orchestrate(deadLetterMessage, deadLetterTopics);

        logger.info("FINISH ::: Processing event response");
    }
}
