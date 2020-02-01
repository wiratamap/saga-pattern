package com.personal.sagapattern.core.service;

import com.personal.sagapattern.core.enumeration.Status;
import com.personal.sagapattern.core.model.TopUpAction;
import com.personal.sagapattern.core.model.dto.TopUpRequest;
import com.personal.sagapattern.core.model.dto.TopUpResponse;
import com.personal.sagapattern.core.repository.TopUpActionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WalletService {

    private final TopUpActionRepository topUpActionRepository;

    public TopUpResponse topUp(TopUpRequest topUpRequest) {
        TopUpAction topUpAction = TopUpAction.builder()
                .cif(topUpRequest.getCif())
                .amount(topUpRequest.getAmount())
                .wallet(topUpRequest.getWallet())
                .status(Status.PENDING)
                .build();

        TopUpAction topUpResponse = topUpActionRepository.save(topUpAction);

        return TopUpResponse.builder()
                .cif(topUpResponse.getCif())
                .amount(topUpResponse.getAmount())
                .wallet(topUpResponse.getWallet())
                .status(topUpResponse.getStatus())
                .build();
    }
}
