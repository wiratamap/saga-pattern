package com.personal.sagapattern.core.event_top_up;

import java.util.UUID;

import com.personal.sagapattern.core.event_top_up.model.EventTopUp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventTopUpRepository extends JpaRepository<EventTopUp, UUID> {
}
