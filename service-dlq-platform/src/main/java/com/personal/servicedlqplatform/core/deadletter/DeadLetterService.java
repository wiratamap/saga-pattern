package com.personal.servicedlqplatform.core.deadletter;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeadLetterService {

    private final DeadLetterRepository deadLetterRepository;

    public DeadLetter create(DeadLetter newDeadLetter) {
        return this.deadLetterRepository.save(newDeadLetter);
    }

	public List<DeadLetter> fetchAll() {
		return this.deadLetterRepository.findAll();
	}

}
