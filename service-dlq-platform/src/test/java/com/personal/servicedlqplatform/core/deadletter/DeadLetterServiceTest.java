package com.personal.servicedlqplatform.core.deadletter;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @InjectMocks
    private DeadLetterService deadLetterService;

    private UUID mockDeadLetterId = UUID.fromString("7b5f770a-68e9-4723-bcad-8cb8c12f362d");

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
}
