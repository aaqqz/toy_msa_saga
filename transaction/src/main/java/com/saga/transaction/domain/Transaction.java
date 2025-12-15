package com.saga.transaction.domain;

import com.saga.common.dto.DepositDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "transactions ")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction {

    @Id
    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "saga_id", nullable = false)
    private String sagaId;

    @Column(name = "from_account_number", nullable = false)
    private String fromAccountNumber;

    @Column(name = "to_account_number", nullable = false)
    private String toAccountNumber;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static Transaction completed(String transactionId, DepositDto.DepositRequest request) {
        Transaction entity = new Transaction();
        entity.transactionId = transactionId;
        entity.sagaId = request.sagaId();
        entity.fromAccountNumber = request.fromAccountNumber();
        entity.toAccountNumber = request.accountNumber();
        entity.amount = request.amount();
        entity.status = "COMPLETED";

        return entity;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = this.createdAt == null ? LocalDateTime.now() : this.createdAt;
    }
}
