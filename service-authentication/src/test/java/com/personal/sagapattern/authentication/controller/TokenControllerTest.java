package com.personal.sagapattern.authentication.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.application_user.ApplicationUser;
import com.personal.sagapattern.application_user.ApplicationUserRepository;
import com.personal.sagapattern.authentication.model.dto.LoginRequestDto;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class TokenControllerTest {
    @Autowired
    private MockMvc client;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @AfterEach
    public void tearDown() {
        this.applicationUserRepository.deleteAll();
    }

    @Test
    void login_shouldReturnValidTokenAndHttpStatusOk_whenLoginIsSuccess() throws Exception {
        ApplicationUser applicationUser = ApplicationUser.builder().email("john.doe@mail.com")
                .name("John Doe").password(passwordEncoder.encode("P@ssw0rd")).build();
        applicationUserRepository.save(applicationUser);
        LoginRequestDto loginRequestDto = LoginRequestDto.builder().email("john.doe@mail.com").password("P@ssw0rd").build();
        String loginRequest = objectMapper.writeValueAsString(loginRequestDto);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/tokens").content(loginRequest)
                .contentType(MediaType.APPLICATION_JSON);

        client.perform(request).andExpect(status().isOk());
    }

    @Test
    void login_shouldReturnHttpStatus401Unauthorized_whenUserNotFoundOrUsernameAndOrPasswordAreInvalid()
            throws Exception {
        LoginRequestDto loginRequestDto = LoginRequestDto.builder().email("john.doe@mail.com").password("P@ssw0rd").build();
        String loginRequest = objectMapper.writeValueAsString(loginRequestDto);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/tokens").content(loginRequest)
                .contentType(MediaType.APPLICATION_JSON);

        client.perform(request).andExpect(status().isUnauthorized());
    }
}
