package com.personal.sagapattern.core;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.core.exception.AccountNotFoundException;
import com.personal.sagapattern.core.exception.ExceededBalanceException;
import com.personal.sagapattern.core.model.Account;
import com.personal.sagapattern.core.model.dto.TopUpEventResponse;
import com.personal.sagapattern.core.model.dto.TopUpRequest;
import com.personal.sagapattern.core.model.dto.TransferRequest;
import com.personal.sagapattern.orchestration.service.SagaOrchestrationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AccountService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final AccountRepository accountRepository;

    private final SagaOrchestrationService sagaOrchestrationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${event.transfer.topics}")
    private List<String> transferEventTopics;

    @Value("${event.failed-top-up.topics}")
    private List<String> failedTopUpEventTopics;

    @Value("${event.success-top-up.topics}")
    private List<String> successTopUpEventTopics;

    private boolean isAmountExceedsAccountBalance(TopUpRequest topUpRequest, Account account) {
        return topUpRequest.getAmount() > account.getBalance();
    }

    private String buildTopUpEventResponseMessage(TopUpRequest topUpRequest, String reason)
            throws JsonProcessingException {
        TopUpEventResponse failedTopUpEvent = TopUpEventResponse.builder().eventId(topUpRequest.getEventId())
                .cif(topUpRequest.getCif()).amount(topUpRequest.getAmount()).wallet(topUpRequest.getWallet())
                .destinationOfFund(topUpRequest.getDestinationOfFund()).reason(reason).build();
        return objectMapper.writeValueAsString(failedTopUpEvent);
    }

    private void orchestrateFailedTopUpEvent(TopUpRequest topUpRequest, String reason) throws JsonProcessingException {
        logger.error("{}, CIF: {}", reason, topUpRequest.getCif());

        String failedTopUpEventResponse = buildTopUpEventResponseMessage(topUpRequest, reason);

        sagaOrchestrationService.orchestrate(failedTopUpEventResponse, failedTopUpEventTopics);
    }

    private void orchestrateSuccessTopUpEvent(TopUpRequest topUpRequest) throws JsonProcessingException {
        TransferRequest transferRequest = TransferRequest.builder().eventId(topUpRequest.getEventId())
                .cif(topUpRequest.getCif()).amount(topUpRequest.getAmount())
                .destinationOfFund(topUpRequest.getDestinationOfFund()).build();
        String transferRequestEvent = objectMapper.writeValueAsString(transferRequest);

        logger.info("{}, CIF: {}", "SUCCESS", topUpRequest.getCif());

        String successTopUpEventResponse = buildTopUpEventResponseMessage(topUpRequest, "SUCCESS");

        sagaOrchestrationService.orchestrate(transferRequestEvent, transferEventTopics);
        sagaOrchestrationService.orchestrate(successTopUpEventResponse, successTopUpEventTopics);
    }

    public void topUp(TopUpRequest topUpRequest) throws JsonProcessingException {
        Account account = accountRepository.findByCif(topUpRequest.getCif());

        if (Objects.isNull(account)) {
            String reason = "Source account with CIF " + topUpRequest.getCif() + " not found";
            orchestrateFailedTopUpEvent(topUpRequest, reason);
            throw new AccountNotFoundException(reason);
        }

        if (isAmountExceedsAccountBalance(topUpRequest, account)) {
            String reason = "Top-up amount exceeds account balance";
            orchestrateFailedTopUpEvent(topUpRequest, reason);
            throw new ExceededBalanceException(reason);
        }

        long newBalance = account.getBalance() - topUpRequest.getAmount();
        account.setBalance(newBalance);
        accountRepository.save(account);

        orchestrateSuccessTopUpEvent(topUpRequest);
    }
}
