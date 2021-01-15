package com.personal.sagapattern.core;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.core.exception.EventNotFoundException;
import com.personal.sagapattern.core.model.EventTopUp;
import com.personal.sagapattern.core.model.Status;
import com.personal.sagapattern.core.model.dto.TopUpEventResult;
import com.personal.sagapattern.core.model.dto.TopUpRequest;
import com.personal.sagapattern.core.model.dto.TopUpResponse;
import com.personal.sagapattern.orchestration.service.SagaOrchestrationService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class WalletService {

    private final SagaOrchestrationService sagaOrchestrationService;

    private final EventTopUpRepository eventTopUpRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${event.top-up.topics}")
    private List<String> eventTopics;

    public TopUpResponse topUp(TopUpRequest topUpRequest) throws JsonProcessingException {
        EventTopUp eventTopUp = EventTopUp.builder().cif(topUpRequest.getCif()).amount(topUpRequest.getAmount())
                .wallet(topUpRequest.getWallet()).destinationOfFund(topUpRequest.getDestinationOfFund())
                .status(Status.PENDING).build();

        EventTopUp createdEventTopUp = eventTopUpRepository.save(eventTopUp);
        topUpRequest.setEventId(createdEventTopUp.getId());

        String topUpEventRequest = objectMapper.writeValueAsString(topUpRequest);
        sagaOrchestrationService.orchestrate(topUpEventRequest, eventTopics);

        return TopUpResponse.builder().eventId(createdEventTopUp.getId()).cif(createdEventTopUp.getCif())
                .amount(createdEventTopUp.getAmount()).wallet(createdEventTopUp.getWallet())
                .destinationOfFund(createdEventTopUp.getDestinationOfFund()).status(createdEventTopUp.getStatus())
                .build();
    }

    public void updateStatus(TopUpEventResult topUpEventResult, Status status) {
        EventTopUp eventTopUp = eventTopUpRepository.findById(topUpEventResult.getEventId())
                .orElseThrow(EventNotFoundException::new);
        eventTopUp.setStatus(status);
        eventTopUp.setReason(topUpEventResult.getReason());

        eventTopUpRepository.save(eventTopUp);
    }
}
