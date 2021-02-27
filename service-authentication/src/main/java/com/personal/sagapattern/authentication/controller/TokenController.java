package com.personal.sagapattern.authentication.controller;

import com.personal.sagapattern.authentication.jwt.JwtProvider;
import com.personal.sagapattern.authentication.model.dto.JwtResponseDto;
import com.personal.sagapattern.authentication.model.dto.LoginRequestDto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class TokenController {

    private final AuthenticationManager authenticationManager;

    private final JwtProvider jwtProvider;

    private static final Logger logger = LoggerFactory.getLogger(TokenController.class);

    @PostMapping("/tokens")
    public ResponseEntity<JwtResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {
        logger.info("Attempt login to system: {}", loginRequestDto);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateJwtToken(authentication);

        return ResponseEntity.ok(new JwtResponseDto(jwt));
    }
}
