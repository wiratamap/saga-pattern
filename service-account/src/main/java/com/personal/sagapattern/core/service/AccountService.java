package com.personal.sagapattern.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.core.exception.AccountNotFoundException;
import com.personal.sagapattern.core.exception.ExceededBalanceException;
import com.personal.sagapattern.core.model.Account;
import com.personal.sagapattern.core.model.dto.FailedTopUpEvent;
import com.personal.sagapattern.core.model.dto.TopUpRequest;
import com.personal.sagapattern.core.model.dto.TransferRequest;
import com.personal.sagapattern.core.repository.AccountRepository;
import com.personal.sagapattern.orchestration.service.SagaOrchestrationService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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

    public void topUp(TopUpRequest topUpRequest) throws JsonProcessingException {
        Account account = accountRepository.findByCif(topUpRequest.getCif());

        if (Objects.isNull(account)) {
            String reason = "Account not found";
            orchestrateFailedTopUpEvent(topUpRequest, reason);
            throw new AccountNotFoundException(reason);
        }

        if (topUpRequest.getAmount() > account.getBalance()) {
            String reason = "Top-up amount exceeds account balance";
            orchestrateFailedTopUpEvent(topUpRequest, reason);
            throw new ExceededBalanceException(reason);
        }

        int newBalance = account.getBalance() - topUpRequest.getAmount();
        account.setBalance(newBalance);
        accountRepository.save(account);

        TransferRequest transferRequest = TransferRequest.builder()
                .cif(account.getCif())
                .amount(topUpRequest.getAmount())
                .destinationOfFund(topUpRequest.getDestinationOfFund())
                .build();
        String transferRequestEvent = objectMapper.writeValueAsString(transferRequest);

        sagaOrchestrationService.orchestrate(transferRequestEvent, transferEventTopics);
    }

    private void orchestrateFailedTopUpEvent(TopUpRequest topUpRequest, String reason) throws JsonProcessingException {
        logger.error("{}, CIF: {}", reason, topUpRequest.getCif());

        FailedTopUpEvent failedTopUpEvent = FailedTopUpEvent.builder()
                .cif(topUpRequest.getCif())
                .amount(topUpRequest.getAmount())
                .wallet(topUpRequest.getWallet())
                .destinationOfFund(topUpRequest.getDestinationOfFund())
                .reason(reason)
                .build();
        String failedTopUpEventResponse = objectMapper.writeValueAsString(failedTopUpEvent);

        sagaOrchestrationService.orchestrate(failedTopUpEventResponse, failedTopUpEventTopics);
    }
}
