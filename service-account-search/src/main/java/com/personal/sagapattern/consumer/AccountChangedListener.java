package com.personal.sagapattern.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.core.AccountService;
import com.personal.sagapattern.core.model.Account;
import com.personal.sagapattern.core.model.dto.AccountDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountChangedListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AccountService accountService;

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${event.account-created.topic}")
    void consumeCreatedAccountEvent(@Payload String message) throws JsonProcessingException {
        AccountDto accountDto = objectMapper.readValue(message, AccountDto.class);
        Account account = accountDto.convertTo(Account.class);
        logger.info("Account create action detected, data: {}", accountDto);
        logger.info("START ::: Processing Event");

        accountService.create(account);

        logger.info("FINISH ::: Processing Event");
    }

    @KafkaListener(topics = "${event.account-updated.topic}")
    void consumeUpdatedAccountEvent(@Payload String message) throws JsonProcessingException {
        AccountDto accountDto = objectMapper.readValue(message, AccountDto.class);
        Account account = accountDto.convertTo(Account.class);
        logger.info("Account update action detected, data: {}", accountDto);
        logger.info("START ::: Processing Event");

        accountService.update(account);

        logger.info("FINISH ::: Processing Event");
    }
}
