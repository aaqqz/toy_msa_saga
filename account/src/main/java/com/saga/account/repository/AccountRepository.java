package com.saga.account.repository;

import com.saga.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {

    Account findByAccountNumber(String accountNumber);
}
