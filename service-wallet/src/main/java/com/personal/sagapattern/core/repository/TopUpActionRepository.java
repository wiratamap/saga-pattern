package com.personal.sagapattern.core.repository;

import com.personal.sagapattern.core.model.TopUpAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TopUpActionRepository extends JpaRepository<TopUpAction, UUID> {
}
