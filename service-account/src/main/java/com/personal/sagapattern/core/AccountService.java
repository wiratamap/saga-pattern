package com.personal.sagapattern.core;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.core.exception.AccountNotFoundException;
import com.personal.sagapattern.core.exception.ExceededBalanceException;
import com.personal.sagapattern.core.model.Account;
import com.personal.sagapattern.core.model.event.EventTransactionAccountInformation;
import com.personal.sagapattern.core.model.event.EventTransactionRequest;
import com.personal.sagapattern.core.model.event.EventTransactionResponse;
import com.personal.sagapattern.core.model.event.TransactionType;
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

    private final ObjectMapper objectMapper;

    @Value("${event.failed-transaction.topics}")
    private List<String> failedTransactionEventTopics;

    @Value("${event.success-transaction.topics}")
    private List<String> successTransactionEventTopics;

    @Value("${event.account-updated.topics}")
    private List<String> accountUpdatedEventTopics;

    private String buildTransactionEventResponseMessage(EventTransactionRequest eventTransactionRequest, String reason)
            throws JsonProcessingException {
        EventTransactionResponse eventTransactionResponse = eventTransactionRequest
                .convertTo(EventTransactionResponse.class);
        eventTransactionResponse.setFailReason(reason);

        return objectMapper.writeValueAsString(eventTransactionResponse);
    }

    private void orchestrateFailedTransactionEvent(EventTransactionRequest eventTransactionRequest, String reason)
            throws JsonProcessingException {
        logger.error(reason);
        String failedTransactionEventResponse = this.buildTransactionEventResponseMessage(eventTransactionRequest,
                reason);
        sagaOrchestrationService.orchestrate(failedTransactionEventResponse, failedTransactionEventTopics);
    }

    private void orchestrateSuccessTransactionEvent(EventTransactionRequest eventTransactionRequest,
            Account sourceAccount, Account destinationAccount) throws JsonProcessingException {
        logger.info("Success processing transaction, data: {}", eventTransactionRequest);

        EventTransactionAccountInformation sourceAccountInformation = sourceAccount.getAccountDetail()
                .convertTo(EventTransactionAccountInformation.class);
        eventTransactionRequest.setSourceAccountInformation(sourceAccountInformation);

        if (!Objects.isNull(destinationAccount)) {
            EventTransactionAccountInformation destinationAccountInformation = destinationAccount.getAccountDetail()
                    .convertTo(EventTransactionAccountInformation.class);
            eventTransactionRequest.setDestinationAccountInformation(destinationAccountInformation);
        }
        String successTransactionEventResponse = this.buildTransactionEventResponseMessage(eventTransactionRequest,
                null);
        String sourceAccountUpdatedEvent = this.objectMapper.writeValueAsString(sourceAccount);
        String destinationAccountUpdatedEvent = this.objectMapper.writeValueAsString(destinationAccount);

        if (!Objects.isNull(destinationAccount)) {
            sagaOrchestrationService.orchestrate(destinationAccountUpdatedEvent, accountUpdatedEventTopics);
        }
        sagaOrchestrationService.orchestrate(sourceAccountUpdatedEvent, accountUpdatedEventTopics);
        sagaOrchestrationService.orchestrate(successTransactionEventResponse, successTransactionEventTopics);
    }

    private boolean isAmountExceedsAccountBalance(EventTransactionRequest eventTransactionRequest, Account account) {
        return eventTransactionRequest.getAmount() > account.getBalance();
    }

    private Account updateAccountBalance(String externalAccountNumber, EventTransactionRequest eventTransactionRequest,
            TransactionType transactionType) throws JsonProcessingException {
        Account account = accountRepository.findByAccountDetailExternalAccountNumber(externalAccountNumber);

        if (Objects.isNull(account)) {
            String reason = "Account with External Account Number: " + externalAccountNumber + " not found";
            this.orchestrateFailedTransactionEvent(eventTransactionRequest, reason);
            throw new AccountNotFoundException(reason);
        }

        if (isAmountExceedsAccountBalance(eventTransactionRequest, account)) {
            String reason = "Transaction exceeds account balance";
            this.orchestrateFailedTransactionEvent(eventTransactionRequest, reason);
            throw new ExceededBalanceException(reason);
        }

        transactionType.updateBalance(account, eventTransactionRequest);
        return accountRepository.save(account);
    }

    public void processTransaction(EventTransactionRequest eventTransactionRequest) throws JsonProcessingException {
        String sourceExternalAccountNumber = eventTransactionRequest.getSourceAccountInformation()
                .getExternalAccountNumber();
        Account sourceAccount = this.updateAccountBalance(sourceExternalAccountNumber, eventTransactionRequest,
                TransactionType.DEBIT);
        Account destinationAccount = null;

        if (eventTransactionRequest.getDestinationAccountInformation().getAccountProvider().equals("MeBank")) {
            String destinationExternalAccountNumber = eventTransactionRequest.getDestinationAccountInformation()
                    .getExternalAccountNumber();
            destinationAccount = this.updateAccountBalance(destinationExternalAccountNumber, eventTransactionRequest,
                    TransactionType.CREDIT);
        }

        this.orchestrateSuccessTransactionEvent(eventTransactionRequest, sourceAccount, destinationAccount);
    }
}
