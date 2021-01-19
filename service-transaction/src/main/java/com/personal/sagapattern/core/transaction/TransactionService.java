package com.personal.sagapattern.core.transaction;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.common.enumeration.Status;
import com.personal.sagapattern.core.event_top_up.EventTopUpRepository;
import com.personal.sagapattern.core.event_top_up.exception.EventNotFoundException;
import com.personal.sagapattern.core.event_top_up.exception.TransactionDetailNotFoundException;
import com.personal.sagapattern.core.event_top_up.model.EventTopUp;
import com.personal.sagapattern.core.event_top_up.model.dto.TopUpEventResult;
import com.personal.sagapattern.core.event_top_up.model.dto.TopUpRequest;
import com.personal.sagapattern.core.event_top_up.model.dto.TopUpResponse;
import com.personal.sagapattern.core.transaction.model.Transaction;
import com.personal.sagapattern.core.transaction.model.TransactionDetail;
import com.personal.sagapattern.core.transaction.model.TransactionType;
import com.personal.sagapattern.core.transaction.model.dto.CreateTransactionRequestDto;
import com.personal.sagapattern.core.transaction.model.event.EventTransactionAccountInformation;
import com.personal.sagapattern.core.transaction.model.event.EventTransactionRequest;
import com.personal.sagapattern.orchestration.service.SagaOrchestrationService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class TransactionService {

    private final SagaOrchestrationService sagaOrchestrationService;

    private final EventTopUpRepository eventTopUpRepository;

    private final TransactionRepository transactionRepository;

    private final ObjectMapper objectMapper;

    @Value("${event.transaction.topics}")
    private List<String> transactionEventTopics;

    public TopUpResponse topUp(TopUpRequest topUpRequest) throws JsonProcessingException {
        EventTopUp eventTopUp = EventTopUp.builder().cif(topUpRequest.getCif()).amount(topUpRequest.getAmount())
                .wallet(topUpRequest.getWallet()).destinationOfFund(topUpRequest.getDestinationOfFund())
                .status(Status.PENDING).build();

        EventTopUp createdEventTopUp = eventTopUpRepository.save(eventTopUp);
        topUpRequest.setEventId(createdEventTopUp.getId());

        String topUpEventRequest = objectMapper.writeValueAsString(topUpRequest);
        sagaOrchestrationService.orchestrate(topUpEventRequest, transactionEventTopics);

        return TopUpResponse.builder().eventId(createdEventTopUp.getId()).cif(createdEventTopUp.getCif())
                .amount(createdEventTopUp.getAmount()).wallet(createdEventTopUp.getWallet())
                .destinationOfFund(createdEventTopUp.getDestinationOfFund()).status(createdEventTopUp.getStatus())
                .build();
    }

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

    public void updateStatus(TopUpEventResult topUpEventResult, Status status) {
        EventTopUp eventTopUp = eventTopUpRepository.findById(topUpEventResult.getEventId())
                .orElseThrow(EventNotFoundException::new);
        eventTopUp.setStatus(status);
        eventTopUp.setReason(topUpEventResult.getReason());

        eventTopUpRepository.save(eventTopUp);
    }
}
