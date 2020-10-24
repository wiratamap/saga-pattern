package com.personal.servicedlqplatform.core.deadletter;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeadLetterService {

    private final DeadLetterRepository deadLetterRepository;

    public DeadLetter create(DeadLetter newDeadLetter) {
        return deadLetterRepository.save(newDeadLetter);
    }

}
