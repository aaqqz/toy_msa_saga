package com.saga.account.domain;

import com.saga.common.dto.TransferDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

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

    public static AccountTransaction withdraw(String sagaId, TransferDto.TransferRequest request, Account fromAccount) {
        // 출금
        AccountTransaction entity = new AccountTransaction();
        entity.transactionId = UUID.randomUUID().toString();
        entity.accountId = fromAccount.getAccountId();
        entity.amount = request.amount();
        entity.transactionType = "WITHDRAW";
        entity.sagaId = sagaId;
        entity.status = "COMPLETED";

        return entity;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = this.createdAt == null ? LocalDateTime.now() : this.createdAt;
    }

    public void compensated() {
        this.status = "COMPENSATED";
    }
}
