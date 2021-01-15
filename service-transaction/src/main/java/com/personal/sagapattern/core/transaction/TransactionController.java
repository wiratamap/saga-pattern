package com.personal.sagapattern.core.transaction;

import javax.validation.Valid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.personal.sagapattern.core.event_top_up.model.dto.TopUpRequest;
import com.personal.sagapattern.core.event_top_up.model.dto.TopUpResponse;

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
