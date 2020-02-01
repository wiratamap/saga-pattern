package com.personal.sagapattern.core.controller;

import com.personal.sagapattern.core.model.dto.TopUpRequest;
import com.personal.sagapattern.core.model.dto.TopUpResponse;
import com.personal.sagapattern.core.service.WalletService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("wallet")
@AllArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("top-up")
    public ResponseEntity topUp(@Valid @RequestBody TopUpRequest topUpRequest) {
        TopUpResponse topUpResponse = walletService.topUp(topUpRequest);

        return ResponseEntity.ok(topUpResponse);
    }
}
