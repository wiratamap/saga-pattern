package com.personal.sagapattern.core.transaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
import com.personal.sagapattern.core.transaction.model.dto.CreateTransactionRequestDto;
import com.personal.sagapattern.core.transaction.model.dto.DestinationAccountInformationDto;
import com.personal.sagapattern.orchestration.exception.OrchestrationException;
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
    private EventTopUpRepository eventTopUpRepository;

    @Mock
    private TransactionRepository transactionRepository;

    private List<String> eventTopics = Arrays.asList("EVENT_TRANSACTION_REQUEST", "SURROUNDING_NOTIFICATION",
            "TRANSFER_NOTIFICATION");

    private ObjectMapper objectMapper = new ObjectMapper();

    private UUID mockEventId = UUID.fromString("7b5f770a-68e9-4723-bcad-8cb8c12f362d");

    private TopUpRequest topUpRequest = TopUpRequest.builder().eventId(mockEventId).cif("000000001").amount(10000)
            .wallet("GO-PAY").destinationOfFund("00000000").build();

    private TopUpEventResult topUpEventResult = TopUpEventResult.builder().eventId(mockEventId).cif("000000001")
            .amount(10000).wallet("GO-PAY").destinationOfFund("00000000").reason("REASON").build();

    private void mockSaveOnTopUpActionRepository() {
        Mockito.when(eventTopUpRepository.save(any(EventTopUp.class))).then(new Answer<EventTopUp>() {
            @Override
            public EventTopUp answer(InvocationOnMock invocation) throws Throwable {
                EventTopUp eventTopUp = invocation.getArgument(0);
                eventTopUp.setId(mockEventId);

                return eventTopUp;
            }
        });
    }

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

    private void mockEmptyTransactionDetailsSaveOnTransactionRepository() {
        Mockito.when(transactionRepository.save(any(Transaction.class))).then(new Answer<Transaction>() {
            @Override
            public Transaction answer(InvocationOnMock invocation) throws Throwable {
                Transaction transaction = invocation.getArgument(0);
                transaction.setId(mockEventId);
                transaction.setTransactionDetails(Collections.emptyList());

                return transaction;
            }
        });
    }

    @BeforeEach
    void setUp() {
        this.transactionService = new TransactionService(sagaOrchestrationService, eventTopUpRepository,
                transactionRepository, objectMapper, eventTopics);
    }

    @AfterEach
    void tearDown() {
        clearInvocations(eventTopUpRepository, sagaOrchestrationService);
    }

    @Test
    void topUp_shouldReturnTopUpResponseWithPendingStatus_whenTopUpIsInvoked() throws JsonProcessingException {
        mockSaveOnTopUpActionRepository();
        String topUpEvent = objectMapper.writeValueAsString(topUpRequest);

        TopUpResponse topUpResponse = transactionService.topUp(topUpRequest);

        verify(eventTopUpRepository).save(any(EventTopUp.class));
        verify(sagaOrchestrationService).orchestrate(topUpEvent, eventTopics);
        assertEquals(Status.PENDING, topUpResponse.getStatus());
    }

    @Test
    void topUp_shouldNotSaveTopUpEvent_whenFailedToOrchestrateTriggeredEvent() {
        mockSaveOnTopUpActionRepository();
        doThrow(OrchestrationException.class).when(sagaOrchestrationService).orchestrate(anyString(), anyList());

        Executable topUpAction = () -> transactionService.topUp(topUpRequest);

        verify(eventTopUpRepository, never()).save(any(EventTopUp.class));
        assertThrows(OrchestrationException.class, topUpAction);
    }

    @Test
    void updateStatus_shouldSaveEventWithSuccessStatus_whenNewStatusIsSuccess() {
        ArgumentCaptor<EventTopUp> eventTopUpArgumentCaptor = ArgumentCaptor.forClass(EventTopUp.class);
        EventTopUp eventTopUp = new EventTopUp();
        eventTopUp.setStatus(Status.SUCCESS);
        when(eventTopUpRepository.findById(mockEventId)).thenReturn(Optional.of(eventTopUp));

        transactionService.updateStatus(topUpEventResult, Status.SUCCESS);

        verify(eventTopUpRepository).save(eventTopUpArgumentCaptor.capture());
        assertEquals(Status.SUCCESS, eventTopUpArgumentCaptor.getValue().getStatus());
    }

    @Test
    void updateStatus_shouldNotUpdateStatusAndThrowEventNotFound_whenEventIsNotFound() {
        when(eventTopUpRepository.findById(mockEventId)).thenReturn(Optional.empty());

        Executable updateStatusAction = () -> transactionService.updateStatus(topUpEventResult, Status.SUCCESS);

        verify(eventTopUpRepository, never()).save(any(EventTopUp.class));
        assertThrows(EventNotFoundException.class, updateStatusAction);
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
    void create_shouldThrowTransactionDetailNotFoundException() throws JsonProcessingException {
        this.mockEmptyTransactionDetailsSaveOnTransactionRepository();
        DestinationAccountInformationDto destinationAccountInformationDto = DestinationAccountInformationDto.builder()
                .accountHolderName("Bertha Doe").accountProvider("GO-PAY").externalAccountNumber("987654321").build();
        CreateTransactionRequestDto createTransactionRequestDto = CreateTransactionRequestDto.builder()
                .sourceExternalAccountNumber("123456789")
                .destinationAccountInformation(destinationAccountInformationDto).amount(100_000).currency("IDR")
                .build();

        Executable createAction = () -> this.transactionService.create(createTransactionRequestDto);

        Assertions.assertThrows(TransactionDetailNotFoundException.class, createAction);
        Mockito.verify(sagaOrchestrationService, Mockito.never()).orchestrate(Mockito.anyString(),
                Mockito.eq(eventTopics));
    }
}
