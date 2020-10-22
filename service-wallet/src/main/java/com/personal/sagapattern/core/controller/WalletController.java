package com.personal.sagapattern.core.controller;

import javax.validation.Valid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.personal.sagapattern.core.model.dto.TopUpRequest;
import com.personal.sagapattern.core.model.dto.TopUpResponse;
import com.personal.sagapattern.core.service.WalletService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("wallet")
@AllArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("top-up")
    public ResponseEntity<TopUpResponse> topUp(@Valid @RequestBody TopUpRequest topUpRequest)
            throws JsonProcessingException {
        TopUpResponse topUpResponse = walletService.topUp(topUpRequest);

        return ResponseEntity.ok(topUpResponse);
    }
}
