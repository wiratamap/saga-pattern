package com.personal.sagapattern.core.transaction;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.common.enumeration.Status;
import com.personal.sagapattern.core.transaction.exception.TransactionDetailNotFoundException;
import com.personal.sagapattern.core.transaction.exception.TransactionNotFoundException;
import com.personal.sagapattern.core.transaction.model.Transaction;
import com.personal.sagapattern.core.transaction.model.TransactionDetail;
import com.personal.sagapattern.core.transaction.model.TransactionType;
import com.personal.sagapattern.core.transaction.model.dto.CreateTransactionRequestDto;
import com.personal.sagapattern.core.transaction.model.event.EventTransactionAccountInformation;
import com.personal.sagapattern.core.transaction.model.event.EventTransactionRequest;
import com.personal.sagapattern.core.transaction.model.event.EventTransactionResponse;
import com.personal.sagapattern.orchestration.service.SagaOrchestrationService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TransactionService {

    private final SagaOrchestrationService sagaOrchestrationService;

    private final TransactionRepository transactionRepository;

    private final ObjectMapper objectMapper;

    @Value("${event.transaction.topics}")
    private List<String> transactionEventTopics;

    private TransactionDetail getTransactionDetailByTransactionType(Transaction createdTransaction,
            TransactionType transactionType) {
        return createdTransaction.getTransactionDetails().stream()
                .filter(transactionDetail -> transactionDetail.getTransactionType().equals(transactionType)).findFirst()
                .orElseThrow(TransactionDetailNotFoundException::new);
    }

    private void orchestrateTransactionRequest(Transaction createdTransaction) throws JsonProcessingException {
        TransactionDetail debitedAccount = this.getTransactionDetailByTransactionType(createdTransaction,
                TransactionType.DEBIT);
        TransactionDetail creditedAccount = this.getTransactionDetailByTransactionType(createdTransaction,
                TransactionType.CREDIT);

        EventTransactionAccountInformation sourceAccount = debitedAccount
                .convertTo(EventTransactionAccountInformation.class);
        EventTransactionAccountInformation destinationAccount = creditedAccount
                .convertTo(EventTransactionAccountInformation.class);
        EventTransactionRequest transactionRequest = EventTransactionRequest.builder()
                .id(createdTransaction.getId())
                .eventId(createdTransaction.getId())
                .amount(createdTransaction.getAmount()).currency(createdTransaction.getCurrency())
                .note(createdTransaction.getNote()).sourceAccountInformation(sourceAccount)
                .destinationAccountInformation(destinationAccount).build();
        String transactionEventRequest = this.objectMapper.writeValueAsString(transactionRequest);

        this.sagaOrchestrationService.orchestrate(transactionEventRequest, transactionEventTopics);
    }

    @Transactional(rollbackFor = TransactionDetailNotFoundException.class)
    public Transaction create(CreateTransactionRequestDto createTransactionRequest) throws JsonProcessingException {
        TransactionDetail sourceAccountDetail = TransactionDetail.builder()
                .externalAccountNumber(createTransactionRequest.getSourceExternalAccountNumber())
                .accountProvider("MeBank").transactionType(TransactionType.DEBIT).build();
        TransactionDetail destinationAccountDetail = createTransactionRequest.getDestinationAccountInformation()
                .convertTo(TransactionDetail.class);
        destinationAccountDetail.setTransactionType(TransactionType.CREDIT);
        List<TransactionDetail> transactionDetails = Arrays.asList(sourceAccountDetail, destinationAccountDetail);

        Transaction transaction = createTransactionRequest.convertTo(Transaction.class);
        transaction.setTransactionDetails(transactionDetails);
        transaction.setStatus(Status.PENDING);
        Transaction createdTransaction = this.transactionRepository.save(transaction);

        this.orchestrateTransactionRequest(createdTransaction);

        return createdTransaction;
    }

    public void updateStatus(EventTransactionResponse transactionEventResult, Status status) {
        Transaction transaction = this.transactionRepository.findById(transactionEventResult.getEventId())
                .orElseThrow(TransactionNotFoundException::new);
        transaction.setStatus(status);
        transaction.setFailReason(transactionEventResult.getFailReason());

        this.transactionRepository.save(transaction);
    }
}
