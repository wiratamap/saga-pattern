package com.personal.sagapattern.core;

import javax.validation.Valid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.personal.sagapattern.core.model.dto.TopUpRequest;
import com.personal.sagapattern.core.model.dto.TopUpResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transactions")
    public ResponseEntity<TopUpResponse> topUp(@Valid @RequestBody TopUpRequest topUpRequest)
            throws JsonProcessingException {
        TopUpResponse topUpResponse = transactionService.topUp(topUpRequest);

        return ResponseEntity.ok(topUpResponse);
    }
}
