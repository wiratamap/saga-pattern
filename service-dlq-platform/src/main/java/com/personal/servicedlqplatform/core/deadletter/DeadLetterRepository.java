package com.personal.servicedlqplatform.core.deadletter;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeadLetterRepository extends JpaRepository<DeadLetter, UUID> {
    List<DeadLetter> findByOriginalMessageContaining(String originalMessage);
}
