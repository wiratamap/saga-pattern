package com.personal.sagapattern.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.core.enumeration.Status;
import com.personal.sagapattern.core.model.EventTopUp;
import com.personal.sagapattern.core.model.dto.TopUpRequest;
import com.personal.sagapattern.core.model.dto.TopUpResponse;
import com.personal.sagapattern.core.repository.EventTopUpRepository;
import com.personal.sagapattern.orchestration.service.SagaOrchestrationService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class WalletService {

    private final SagaOrchestrationService sagaOrchestrationService;

    private final EventTopUpRepository eventTopUpRepository;

    @Value("${event.top-up.topics}")
    private List<String> eventTopics;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public TopUpResponse topUp(TopUpRequest topUpRequest) throws JsonProcessingException {
        String topUpEventRequest = objectMapper.writeValueAsString(topUpRequest);
        EventTopUp eventTopUp = EventTopUp.builder()
                .cif(topUpRequest.getCif())
                .amount(topUpRequest.getAmount())
                .wallet(topUpRequest.getWallet())
                .status(Status.PENDING)
                .build();

        sagaOrchestrationService.orchestrate(topUpEventRequest, eventTopics);
        EventTopUp topUpResponse = eventTopUpRepository.save(eventTopUp);

        return TopUpResponse.builder()
                .cif(topUpResponse.getCif())
                .amount(topUpResponse.getAmount())
                .wallet(topUpResponse.getWallet())
                .status(topUpResponse.getStatus())
                .build();
    }
}
