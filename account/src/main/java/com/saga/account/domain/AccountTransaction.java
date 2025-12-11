package com.saga.account.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "account_transactions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountTransaction {

    @Id
    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "account_id", nullable = false)
    private String  accountId;

    @Column(nullable = false)
    private BigDecimal amount;

    // TODO to enum
    @Column(name = "transaction_type", nullable = false)
    private String transactionType;

    @Column(name = "saga_id")
    private String sagaId;

    // TODO to enum
    @Column(nullable = false)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = this.createdAt == null ? LocalDateTime.now() : this.createdAt;
    }
}
