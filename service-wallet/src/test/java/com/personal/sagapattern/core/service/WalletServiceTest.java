package com.personal.sagapattern.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.core.enumeration.Status;
import com.personal.sagapattern.core.model.EventTopUp;
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
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    private WalletService walletService;

    @Mock
    private SagaOrchestrationService sagaOrchestrationService;

    @Mock
    private EventTopUpRepository eventTopUpRepository;

    private List<String> eventTopics = eventTopics();

    private ObjectMapper objectMapper = new ObjectMapper();

    private void mockSaveOnTopUpActionRepository() {
        when(eventTopUpRepository.save(any(EventTopUp.class))).then(new Answer<EventTopUp>() {
            @Override
            public EventTopUp answer(InvocationOnMock invocation) throws Throwable {
                EventTopUp eventTopUp = invocation.getArgument(0);
                eventTopUp.setId(UUID.randomUUID());

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
        TopUpRequest topUpRequest = TopUpRequest.builder()
                .cif("000000001")
                .amount(10000)
                .wallet("GO-PAY")
                .build();
        String topUpEvent = objectMapper.writeValueAsString(topUpRequest);

        TopUpResponse topUpResponse = walletService.topUp(topUpRequest);

        verify(eventTopUpRepository).save(any(EventTopUp.class));
        verify(sagaOrchestrationService).orchestrate(topUpEvent, eventTopics);
        assertEquals(Status.PENDING, topUpResponse.getStatus());
    }

    @Test
    void topUp_shouldNotSaveTopUpEvent_whenFailedToOrchestrateTriggeredEvent() {
        doThrow(OrchestrationException.class).when(sagaOrchestrationService).orchestrate(anyString(), anyList());
        TopUpRequest topUpRequest = TopUpRequest.builder()
                .cif("000000001")
                .amount(10000)
                .wallet("GO-PAY")
                .build();

        Executable topUpAction = () -> walletService.topUp(topUpRequest);

        verify(eventTopUpRepository, never()).save(any(EventTopUp.class));
        assertThrows(OrchestrationException.class, topUpAction);
    }
}
