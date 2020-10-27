package com.personal.servicedlqplatform.core.deadletter;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.librarykafkaproducer.orchestration.service.SagaOrchestrationService;
import com.personal.servicedlqplatform.core.deadletter.dto.DeadLetterActionRequestDto;
import com.personal.servicedlqplatform.core.deadletter.dto.DeadLetterDeleteRequestDto;
import com.personal.servicedlqplatform.core.deadletter.exception.DeadLetterNotFoundException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

@ExtendWith(MockitoExtension.class)
class DeadLetterServiceTest {

    @Mock
    private DeadLetterRepository deadLetterRepository;

    @Mock
    private SagaOrchestrationService sagaOrchestrationService;

    @InjectMocks
    private DeadLetterService deadLetterService;

    private UUID mockDeadLetterId = UUID.fromString("7b5f770a-68e9-4723-bcad-8cb8c12f362d");

    private ObjectMapper objectMapper = new ObjectMapper();

    private void mockSaveOnTopUpActionRepository() {
        Mockito.when(deadLetterRepository.save(Mockito.any(DeadLetter.class))).then(new Answer<DeadLetter>() {
            @Override
            public DeadLetter answer(InvocationOnMock invocation) throws Throwable {
                DeadLetter eventTopUp = invocation.getArgument(0);
                eventTopUp.setId(mockDeadLetterId);

                return eventTopUp;
            }
        });
    }

    @AfterEach
    public void tearDown() {
        Mockito.clearInvocations(this.deadLetterRepository, this.sagaOrchestrationService);
    }

    private DeadLetter deadLetter() {
        OriginTopic originalTopic = OriginTopic.builder().name("ORIGINAL_TOPIC").build();
        List<OriginTopic> originTopics = Collections.singletonList(originalTopic);
        return DeadLetter.builder().originalMessage("something").reason("fail reason").originTopics(originTopics)
                .build();
    }

    @Test
    void create_shouldReturnCreatedDeadLetter_whenCreateWithDefinedDeadLetter() {
        this.mockSaveOnTopUpActionRepository();
        DeadLetter deadLetter = this.deadLetter();

        DeadLetter createdDeadLetter = deadLetterService.create(deadLetter);

        Mockito.verify(this.deadLetterRepository).save(deadLetter);
        Assertions.assertEquals(mockDeadLetterId, createdDeadLetter.getId());
    }

    @Test
    void fetchAll_shouldReturnAvailableDeadLetter_whenThereAreAvailableDeadLetter() {
        List<DeadLetter> deadLetters = Collections.singletonList(this.deadLetter());
        Mockito.when(this.deadLetterRepository.findAll()).thenReturn(deadLetters);

        List<DeadLetter> availableDeadLetters = deadLetterService.fetchAll();

        Assertions.assertEquals(deadLetters, availableDeadLetters);
    }

    @Test
    void delete_shouldOrchestrateBackDeadLetterToOriginTopic_whenDeadLetterActionIsSendToOriginTopic()
            throws JsonProcessingException {
        DeadLetter availableDeadLetter = this.deadLetter();
        availableDeadLetter.setId(this.mockDeadLetterId);
        Mockito.when(this.deadLetterRepository.findById(availableDeadLetter.getId()))
                .thenReturn(Optional.of(availableDeadLetter));
        DeadLetterDeleteRequestDto deleteRequest = DeadLetterDeleteRequestDto.builder()
                .deleteAction(DeadLetterActionRequestDto.SEND_TO_ORIGIN_TOPIC).build();
        String expectedMessage = this.objectMapper.writeValueAsString(availableDeadLetter);
        List<String> expectedTopics = Collections.singletonList(availableDeadLetter.getOriginTopics().get(0).getName());

        deadLetterService.delete(availableDeadLetter.getId(), deleteRequest);

        Mockito.verify(this.sagaOrchestrationService, Mockito.atMostOnce()).orchestrate(expectedMessage,
                expectedTopics);
        Mockito.verify(this.deadLetterRepository, Mockito.times(1)).deleteById(availableDeadLetter.getId());
    }

    @Test
    void delete_shouldThrowDeadLetterNotFoundException_whenDeadLetterIsNotExist() throws JsonProcessingException {
        Mockito.when(this.deadLetterRepository.findById(Mockito.any(UUID.class))).thenReturn(Optional.empty());
        DeadLetterDeleteRequestDto deleteRequest = DeadLetterDeleteRequestDto.builder()
                .deleteAction(DeadLetterActionRequestDto.SEND_TO_ORIGIN_TOPIC).build();

        Executable deleteAction = () -> deadLetterService.delete(mockDeadLetterId, deleteRequest);

        Assertions.assertThrows(DeadLetterNotFoundException.class, deleteAction);
    }

    @Test
    void delete_shouldOnlyDeleteAndNotOrchestrateBackTheMessageToOriginTopic_whenDeadLetterActionIsDelete()
            throws JsonProcessingException {
        DeadLetter availableDeadLetter = this.deadLetter();
        availableDeadLetter.setId(this.mockDeadLetterId);
        Mockito.when(this.deadLetterRepository.findById(availableDeadLetter.getId()))
                .thenReturn(Optional.of(availableDeadLetter));
        DeadLetterDeleteRequestDto deleteRequest = DeadLetterDeleteRequestDto.builder()
                .deleteAction(DeadLetterActionRequestDto.SEND_TO_ORIGIN_TOPIC).build();
        String expectedMessage = this.objectMapper.writeValueAsString(availableDeadLetter);
        List<String> expectedTopics = Collections.singletonList(availableDeadLetter.getOriginTopics().get(0).getName());

        deadLetterService.delete(availableDeadLetter.getId(), deleteRequest);

        Mockito.verify(this.sagaOrchestrationService, Mockito.never()).orchestrate(expectedMessage, expectedTopics);
        Mockito.verify(this.deadLetterRepository, Mockito.times(1)).deleteById(availableDeadLetter.getId());
    }
}
