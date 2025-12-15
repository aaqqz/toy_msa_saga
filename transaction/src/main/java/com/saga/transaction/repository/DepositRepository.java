package com.saga.transaction.repository;

import com.saga.transaction.domain.Deposit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositRepository extends JpaRepository<Deposit, String> {
}
