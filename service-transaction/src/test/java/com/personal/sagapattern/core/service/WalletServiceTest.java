package com.personal.sagapattern.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.core.enumeration.Status;
import com.personal.sagapattern.core.exception.EventNotFoundException;
import com.personal.sagapattern.core.model.EventTopUp;
import com.personal.sagapattern.core.model.dto.TopUpEventResult;
import com.personal.sagapattern.core.model.dto.TopUpRequest;
import com.personal.sagapattern.core.model.dto.TopUpResponse;
import com.personal.sagapattern.core.repository.EventTopUpRepository;
import com.personal.sagapattern.orchestration.exception.OrchestrationException;
import com.personal.sagapattern.orchestration.service.SagaOrchestrationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    UUID mockEventId = UUID.fromString("7b5f770a-68e9-4723-bcad-8cb8c12f362d");
    private WalletService walletService;
    @Mock
    private SagaOrchestrationService sagaOrchestrationService;
    @Mock
    private EventTopUpRepository eventTopUpRepository;
    private List<String> eventTopics = eventTopics();

    private ObjectMapper objectMapper = new ObjectMapper();

    private TopUpRequest topUpRequest = TopUpRequest.builder()
            .eventId(mockEventId)
            .cif("000000001")
            .amount(10000)
            .wallet("GO-PAY")
            .destinationOfFund("00000000")
            .build();

    private TopUpEventResult topUpEventResult = TopUpEventResult.builder()
            .eventId(mockEventId)
            .cif("000000001")
            .amount(10000)
            .wallet("GO-PAY")
            .destinationOfFund("00000000")
            .reason("REASON")
            .build();

    private void mockSaveOnTopUpActionRepository() {
        when(eventTopUpRepository.save(any(EventTopUp.class))).then(new Answer<EventTopUp>() {
            @Override
            public EventTopUp answer(InvocationOnMock invocation) throws Throwable {
                EventTopUp eventTopUp = invocation.getArgument(0);
                eventTopUp.setId(mockEventId);

                return eventTopUp;
            }
        });
    }

    private List<String> eventTopics() {
        return Arrays.asList(
                "EVENT_TOP_UP",
                "SURROUNDING_NOTIFICATION",
                "TOP_UP_NOTIFICATION"
        );
    }

    @BeforeEach
    void setUp() {
        walletService = new WalletService(sagaOrchestrationService, eventTopUpRepository, eventTopics);
    }

    @AfterEach
    void tearDown() {
        clearInvocations(
                eventTopUpRepository,
                sagaOrchestrationService
        );
    }

    @Test
    void topUp_shouldReturnTopUpResponseWithPendingStatus_whenTopUpIsInvoked() throws JsonProcessingException {
        mockSaveOnTopUpActionRepository();
        String topUpEvent = objectMapper.writeValueAsString(topUpRequest);

        TopUpResponse topUpResponse = walletService.topUp(topUpRequest);

        verify(eventTopUpRepository).save(any(EventTopUp.class));
        verify(sagaOrchestrationService).orchestrate(topUpEvent, eventTopics);
        assertEquals(Status.PENDING, topUpResponse.getStatus());
    }

    @Test
    void topUp_shouldNotSaveTopUpEvent_whenFailedToOrchestrateTriggeredEvent() {
        mockSaveOnTopUpActionRepository();
        doThrow(OrchestrationException.class).when(sagaOrchestrationService).orchestrate(anyString(), anyList());

        Executable topUpAction = () -> walletService.topUp(topUpRequest);

        verify(eventTopUpRepository, never()).save(any(EventTopUp.class));
        assertThrows(OrchestrationException.class, topUpAction);
    }

    @Test
    void updateStatus_shouldSaveEventWithSuccessStatus_whenNewStatusIsSuccess() {
        ArgumentCaptor<EventTopUp> eventTopUpArgumentCaptor = ArgumentCaptor.forClass(EventTopUp.class);
        EventTopUp eventTopUp = new EventTopUp();
        eventTopUp.setStatus(Status.SUCCESS);
        when(eventTopUpRepository.findById(mockEventId)).thenReturn(Optional.of(eventTopUp));

        walletService.updateStatus(topUpEventResult, Status.SUCCESS);

        verify(eventTopUpRepository).save(eventTopUpArgumentCaptor.capture());
        assertEquals(Status.SUCCESS, eventTopUpArgumentCaptor.getValue().getStatus());
    }

    @Test
    void updateStatus_shouldNotUpdateStatusAndThrowEventNotFound_whenEventIsNotFound() {
        when(eventTopUpRepository.findById(mockEventId)).thenReturn(Optional.empty());

        Executable updateStatusAction = () -> walletService.updateStatus(topUpEventResult, Status.SUCCESS);

        verify(eventTopUpRepository, never()).save(any(EventTopUp.class));
        assertThrows(EventNotFoundException.class, updateStatusAction);
    }
}
