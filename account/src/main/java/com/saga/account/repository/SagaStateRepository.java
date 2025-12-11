package com.saga.account.repository;

import com.saga.account.domain.SagaState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SagaStateRepository extends JpaRepository<SagaState, String> {
}
