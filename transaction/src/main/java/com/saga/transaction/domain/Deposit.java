package com.saga.transaction.domain;

import com.saga.common.dto.DepositDto;
import com.saga.common.event.TransferEvent;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "deposits")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Deposit {

    @Id
    @Column(name = "deposit_id")
    private String depositId;

    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private String status;

    @Column(name = "saga_id")
    private String sagaId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static Deposit completed(String depositId, String transactionId, DepositDto.DepositRequest request) {
        Deposit entity = new Deposit();
        entity.depositId = depositId;
        entity.transactionId = transactionId;
        entity.accountNumber = request.accountNumber();
        entity.amount = request.amount();
        entity.status = "COMPLETED";
        entity.sagaId = request.sagaId();

        return entity;
    }

    // todo TransferEvent.WithdrawSuccessEvent, DepositDto.DepositRequest object 의 필드 변수 naming 확인 필요
    public static Deposit completed(String depositId, String transactionId, TransferEvent.WithdrawSuccessEvent event) {
        Deposit entity = new Deposit();
        entity.depositId = depositId;
        entity.transactionId = transactionId;
        entity.accountNumber = event.toAccountNumber();
        entity.amount = event.amount();
        entity.status = "COMPLETED";
        entity.sagaId = event.sagaId();

        return entity;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = this.createdAt == null ? LocalDateTime.now() : this.createdAt;
    }
}
