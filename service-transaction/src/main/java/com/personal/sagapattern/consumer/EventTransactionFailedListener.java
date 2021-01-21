package com.personal.sagapattern.consumer;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.common.enumeration.Status;
import com.personal.sagapattern.common.model.DeadLetterMessage;
import com.personal.sagapattern.common.model.Disposable;
import com.personal.sagapattern.core.transaction.TransactionService;
import com.personal.sagapattern.core.transaction.model.event.EventTransactionRequest;
import com.personal.sagapattern.core.transaction.model.event.EventTransactionResponse;
import com.personal.sagapattern.orchestration.service.SagaOrchestrationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventTransactionFailedListener {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TransactionService transactionService;

    private final ObjectMapper objectMapper;

    private final SagaOrchestrationService sagaOrchestrationService;

    @Value("${dead-letter.topics}")
    private final List<String> deadLetterTopics;

    @Value("${event.transaction.topics}")
    private final List<String> originTopics;

    @KafkaListener(topics = "${event.failed-transaction.topics}")
    void consume(@Payload String message) throws JsonProcessingException {
        EventTransactionResponse transactionEventResult = objectMapper.readValue(message,
                EventTransactionResponse.class);
        logger.info("Event response detected, data: {}", transactionEventResult);
        logger.info("START ::: Processing event response");

        transactionService.updateStatus(transactionEventResult, Status.FAIL);

        EventTransactionRequest originalMessage = transactionEventResult.convertTo(EventTransactionRequest.class);
        DeadLetterMessage<Disposable> deadLetter = DeadLetterMessage.builder().originTopics(originTopics)
                .originalMessage(originalMessage).reason(transactionEventResult.getFailReason()).build();
        String deadLetterMessage = objectMapper.writeValueAsString(deadLetter);

        sagaOrchestrationService.orchestrate(deadLetterMessage, deadLetterTopics);

        logger.info("FINISH ::: Processing event response");
    }
}
