package com.personal.servicedlqplatform.core.deadletter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.personal.servicedlqplatform.core.deadletter.dto.DeadLetterDeleteRequestDto;
import com.personal.servicedlqplatform.core.deadletter.exception.DeadLetterNotFoundException;
import com.personal.servicedlqplatform.orchestration.service.SagaOrchestrationService;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeadLetterService {

	private final DeadLetterRepository deadLetterRepository;

	private final SagaOrchestrationService sagaOrchestrationService;

	public DeadLetter create(DeadLetter newDeadLetter) {
		return this.deadLetterRepository.save(newDeadLetter);
	}

	public List<DeadLetter> fetchAll() {
		return this.deadLetterRepository.findAll();
	}

	public void delete(UUID deadLetterId, DeadLetterDeleteRequestDto deleteRequest) {
		DeadLetter availableDeadLetter = this.deadLetterRepository.findById(deadLetterId).orElseThrow(
				() -> new DeadLetterNotFoundException("Dead letter with id " + deadLetterId + " not found"));
		String message = availableDeadLetter.getOriginalMessage();
		List<String> originTopics = availableDeadLetter.getOriginTopics().stream().map(OriginTopic::getName)
				.collect(Collectors.toList());

		this.deadLetterRepository.deleteById(deadLetterId);
		deleteRequest.getDeleteAction().commit(sagaOrchestrationService, message, originTopics);
	}
}
