package com.personal.sagapattern.core;

import com.personal.sagapattern.core.model.EventTopUp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventTopUpRepository extends JpaRepository<EventTopUp, UUID> {
}
