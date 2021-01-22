package com.personal.servicedlqplatform.core.deadletter;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.servicedlqplatform.consumer.DeadLetterListener;
import com.personal.servicedlqplatform.core.deadletter.dto.DeadLetterActionRequestDto;
import com.personal.servicedlqplatform.core.deadletter.dto.DeadLetterDeleteRequestDto;
import com.personal.servicedlqplatform.orchestration.service.SagaOrchestrationService;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@MockBean({ DeadLetterListener.class, SagaOrchestrationService.class })
class DeadLetterControllerTest {
    @Autowired
    private MockMvc client;

    @Autowired
    private DeadLetterRepository deadLetterRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void tearDown() {
        this.deadLetterRepository.deleteAll();
    }

    private DeadLetter deadLetter() {
        OriginTopic originalTopic = OriginTopic.builder().name("ORIGINAL_TOPIC").build();
        List<OriginTopic> originTopics = Collections.singletonList(originalTopic);
        return DeadLetter.builder().originalMessage("{\"name\": \"John Doe\"}").reason("fail reason")
                .originTopics(originTopics).build();
    }

    @Test
    void fetchAll_shouldReturnStatusOkAndAvailableDeadLetters_whenGetDeadLettersIsInvoked() throws Exception {
        String expectedOriginTopic = "ORIGINAL_TOPIC";
        this.deadLetterRepository.save(this.deadLetter());
        RequestBuilder request = MockMvcRequestBuilders.get("/dead-letters");

        this.client.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect((ResultMatcher) MockMvcResultMatchers.jsonPath("$[0].originTopics[0].name",
                        Matchers.equalTo(expectedOriginTopic)));
    }

    @Test
    void delete_shouldReturnStatusOk_whenDeleteDeadLettersIsInvoked() throws Exception {
        DeadLetter availableDeadLetter = this.deadLetterRepository.save(this.deadLetter());
        DeadLetterDeleteRequestDto deleteRequest = DeadLetterDeleteRequestDto.builder()
                .deleteAction(DeadLetterActionRequestDto.SEND_TO_ORIGIN_TOPIC).build();
        String deleteRequestJson = this.objectMapper.writeValueAsString(deleteRequest);
        RequestBuilder request = MockMvcRequestBuilders.delete("/dead-letters/{id}", availableDeadLetter.getId())
                .content(deleteRequestJson).contentType(MediaType.APPLICATION_JSON);

        this.client.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
        DeadLetter deletedDeadletter = this.deadLetterRepository.findById(availableDeadLetter.getId()).orElse(null);

        Assertions.assertNull(deletedDeadletter);
    }
}
