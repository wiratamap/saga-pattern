package com.personal.sagapattern.core.transaction;

import javax.validation.Valid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.personal.sagapattern.core.transaction.model.Transaction;
import com.personal.sagapattern.core.transaction.model.dto.CreateTransactionRequestDto;
import com.personal.sagapattern.core.transaction.model.dto.CreateTransactionResponseDto;

import org.springframework.http.HttpStatus;
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
    public ResponseEntity<CreateTransactionResponseDto> create(
            @Valid @RequestBody CreateTransactionRequestDto createTransactionRequestDto)
            throws JsonProcessingException {
        Transaction createdTransaction = this.transactionService.create(createTransactionRequestDto);
        CreateTransactionResponseDto createdTransactionResponse = createdTransaction
                .convertTo(CreateTransactionResponseDto.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdTransactionResponse);
    }
}
