package com.personal.sagapattern.core.transaction;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.common.enumeration.Status;
import com.personal.sagapattern.core.transaction.exception.TransactionNotFoundException;
import com.personal.sagapattern.core.transaction.model.Transaction;
import com.personal.sagapattern.core.transaction.model.TransactionDetail;
import com.personal.sagapattern.core.transaction.model.TransactionType;
import com.personal.sagapattern.core.transaction.model.dto.CreateTransactionRequestDto;
import com.personal.sagapattern.core.transaction.model.dto.DestinationAccountInformationDto;
import com.personal.sagapattern.core.transaction.model.event.EventTransactionAccountInformation;
import com.personal.sagapattern.core.transaction.model.event.EventTransactionResponse;
import com.personal.sagapattern.orchestration.service.SagaOrchestrationService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    private TransactionService transactionService;

    @Mock
    private SagaOrchestrationService sagaOrchestrationService;

    @Mock
    private TransactionRepository transactionRepository;

    private List<String> eventTopics = Arrays.asList("EVENT_TRANSACTION_REQUEST", "SURROUNDING_NOTIFICATION",
            "TRANSFER_NOTIFICATION");

    private ObjectMapper objectMapper = new ObjectMapper();

    private UUID mockEventId = UUID.fromString("7b5f770a-68e9-4723-bcad-8cb8c12f362d");

    private EventTransactionAccountInformation eventTransactionInformation = EventTransactionAccountInformation
            .builder().accountHolderName("John Doe").accountProvider("MeBank").externalAccountNumber("0123456789")
            .transactionType(TransactionType.DEBIT).build();
    private EventTransactionResponse eventTransactionResponse = EventTransactionResponse.builder().amount(100_000)
            .currency("IDR").eventId(mockEventId).sourceAccountInformation(eventTransactionInformation).build();

    private void mockSaveOnTransactionRepository() {
        Mockito.when(transactionRepository.save(any(Transaction.class))).then(new Answer<Transaction>() {
            @Override
            public Transaction answer(InvocationOnMock invocation) throws Throwable {
                Transaction transaction = invocation.getArgument(0);
                transaction.setId(mockEventId);

                return transaction;
            }
        });
    }

    @BeforeEach
    void setUp() {
        this.transactionService = new TransactionService(sagaOrchestrationService, transactionRepository, objectMapper,
                eventTopics);
    }

    @AfterEach
    void tearDown() {
        clearInvocations(transactionRepository, sagaOrchestrationService);
    }

    @Test
    void create_shouldReturnCreatedTransactionAndOrchestrateTransactionRequest_whenCreateIsInvoked()
            throws JsonProcessingException {
        this.mockSaveOnTransactionRepository();
        DestinationAccountInformationDto destinationAccountInformationDto = DestinationAccountInformationDto.builder()
                .accountHolderName("Bertha Doe").accountProvider("GO-PAY").externalAccountNumber("987654321").build();
        CreateTransactionRequestDto createTransactionRequestDto = CreateTransactionRequestDto.builder()
                .sourceExternalAccountNumber("123456789")
                .destinationAccountInformation(destinationAccountInformationDto).amount(100_000).currency("IDR")
                .build();

        Transaction createdTransaction = this.transactionService.create(createTransactionRequestDto);

        Mockito.verify(transactionRepository).save(any(Transaction.class));
        Assertions.assertEquals(100_000, createdTransaction.getAmount());
        Assertions.assertEquals(Status.PENDING, createdTransaction.getStatus());
        Mockito.verify(sagaOrchestrationService).orchestrate(Mockito.anyString(), Mockito.eq(eventTopics));
    }

    @Test
    void updateStatus_shouldSaveTransactionWithSuccessStatus_whenNewStatusIsSuccess() {
        ArgumentCaptor<Transaction> eventTransactionArgumentCaptor = ArgumentCaptor.forClass(Transaction.class);
        TransactionDetail transactionDetail = TransactionDetail.builder().accountHolderName("John Doe")
                .accountProvider("MeBank").transactionType(TransactionType.DEBIT).build();
        Transaction transaction = Transaction.builder().transactionDetails(Collections.singletonList(transactionDetail))
                .build();
        transaction.setStatus(Status.SUCCESS);
        Mockito.when(this.transactionRepository.findById(mockEventId)).thenReturn(Optional.of(transaction));

        this.transactionService.updateStatus(eventTransactionResponse, Status.SUCCESS);

        Mockito.verify(this.transactionRepository).save(eventTransactionArgumentCaptor.capture());
        Assertions.assertEquals(Status.SUCCESS, eventTransactionArgumentCaptor.getValue().getStatus());
    }

    @Test
    void updateStatus_shouldNotUpdateStatusAndThrowTransactionEventNotFoundException_whenTransactionIsNotFound() {
        Mockito.when(this.transactionRepository.findById(mockEventId)).thenReturn(Optional.empty());

        Executable updateStatusAction = () -> transactionService.updateStatus(eventTransactionResponse, Status.SUCCESS);

        Mockito.verify(this.transactionRepository, Mockito.never()).save(Mockito.any(Transaction.class));
        Assertions.assertThrows(TransactionNotFoundException.class, updateStatusAction);
    }
}
