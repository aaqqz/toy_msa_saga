package com.saga.account.domain;

import com.saga.common.dto.TransferDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "saga_state")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SagaState {

    @Id
    @Column(name = "saga_id")
    private String sagaId;

    // TODO to enum
    @Column(name = "pattern_type", nullable = false)
    private String patternType;

    @Column(name = "from_account_id", nullable = false)
    private String fromAccountId;

    @Column(name = "to_account_id", nullable = false)
    private String toAccountId;

    @Column(nullable = false)
    private BigDecimal amount;

    // TODO to enum
    @Column(nullable = false)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static SagaState started(String sagaId, TransferDto.TransferRequest request, Account fromAccount) {
        SagaState entity = new SagaState();
        entity.sagaId = sagaId;
        entity.patternType = "ORCHESTRATION";
        entity.fromAccountId = fromAccount.getAccountId();
        entity.toAccountId = request.toAccountNumber();
        entity.amount = request.amount();
        entity.status = "STARTED";

        return entity;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = this.createdAt == null ? LocalDateTime.now() : this.createdAt;
    }

    public void completed() {
        this.status = "COMPLETED";
    }

    public void compensated() {
        this.status = "COMPENSATED";
    }
}
