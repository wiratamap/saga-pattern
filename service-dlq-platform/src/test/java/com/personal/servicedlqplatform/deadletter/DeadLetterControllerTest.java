package com.personal.servicedlqplatform.deadletter;

import java.util.Collections;
import java.util.List;

import com.personal.servicedlqplatform.consumer.DeadLetterListener;
import com.personal.servicedlqplatform.core.deadletter.DeadLetter;
import com.personal.servicedlqplatform.core.deadletter.DeadLetterRepository;
import com.personal.servicedlqplatform.core.deadletter.OriginTopic;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
@MockBean(DeadLetterListener.class)
class DeadLetterControllerTest {
    @Autowired
    private MockMvc client;

    @Autowired
    private DeadLetterRepository deadLetterRepository;

    @AfterEach
    void tearDown() {
        this.deadLetterRepository.deleteAll();
    }

    private DeadLetter deadLetter() {
        OriginTopic originalTopic = OriginTopic.builder().name("ORIGINAL_TOPIC").build();
        List<OriginTopic> originTopics = Collections.singletonList(originalTopic);
        return DeadLetter.builder().originalMessage("something").reason("fail reason").originTopics(originTopics)
                .build();
    }

    @Test
    void fetchAll_shouldReturnStatusOkAndAvailableDeadLetters_whenGetDeadLettersIsInvoked() throws Exception {
        String expectedOriginalMessage = "something";
        String expectedOriginTopic = "ORIGINAL_TOPIC";
        this.deadLetterRepository.save(this.deadLetter());
        RequestBuilder request = MockMvcRequestBuilders.get("/dead-letters");

        this.client.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect((ResultMatcher) MockMvcResultMatchers.jsonPath("$[0].originalMessage",
                        Matchers.equalTo(expectedOriginalMessage)))
                .andExpect((ResultMatcher) MockMvcResultMatchers.jsonPath("$[0].originTopics[0].name",
                        Matchers.equalTo(expectedOriginTopic)));
    }
}
