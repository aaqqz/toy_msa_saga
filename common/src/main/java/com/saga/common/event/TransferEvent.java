package com.saga.common.event;

import java.math.BigDecimal;

public class TransferEvent {

    public record WithdrawSuccessEvent(
            String sagaId,
            String accountNumber,
            String toAccountNumber,
            BigDecimal amount
    ) {
    }

    public record WithdrawFailedEvent(
            String sagaId,
            String accountNumber,
            String reason
    ) {
    }

    public record DepositSuccessEvent(
            String sagaId,
            String accountNumber,
            BigDecimal amount
    ) {
    }

    public record DepositFailedEvent(
            String sagaId,
            String accountNumber,
            String reason
    ) {
    }

    public record NotificationFailedEvent(
            String sagaId,
            String accountNumber,
            String reason
    ) {
    }
}
