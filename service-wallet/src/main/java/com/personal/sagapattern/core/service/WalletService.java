package com.personal.sagapattern.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.sagapattern.core.enumeration.Status;
import com.personal.sagapattern.core.exception.EventNotFoundException;
import com.personal.sagapattern.core.model.EventTopUp;
import com.personal.sagapattern.core.model.dto.TopUpEventResult;
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
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Value("${event.top-up.topics}")
    private List<String> eventTopics;

    public TopUpResponse topUp(TopUpRequest topUpRequest) throws JsonProcessingException {
        EventTopUp eventTopUp = EventTopUp.builder()
                .cif(topUpRequest.getCif())
                .amount(topUpRequest.getAmount())
                .wallet(topUpRequest.getWallet())
                .destinationOfFund(topUpRequest.getDestinationOfFund())
                .status(Status.PENDING)
                .build();

        EventTopUp topUpResponse = eventTopUpRepository.save(eventTopUp);
        topUpRequest.setEventId(topUpResponse.getId());

        String topUpEventRequest = objectMapper.writeValueAsString(topUpRequest);
        sagaOrchestrationService.orchestrate(topUpEventRequest, eventTopics);

        return TopUpResponse.builder()
                .eventId(topUpResponse.getId())
                .cif(topUpResponse.getCif())
                .amount(topUpResponse.getAmount())
                .wallet(topUpResponse.getWallet())
                .destinationOfFund(topUpResponse.getDestinationOfFund())
                .status(topUpResponse.getStatus())
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
