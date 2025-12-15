package com.saga.common.event;

import java.math.BigDecimal;

public class TransferEvent {

    public record WithdrawSuccessEvent(
            String sagaId,
            String accountNumber,
            String toAccountNumber,
            BigDecimal amount
    ) {
        public static WithdrawSuccessEvent success(String sagaId, String accountNumber, String toAccountNumber, BigDecimal amount) {
            return new WithdrawSuccessEvent(sagaId, accountNumber, toAccountNumber, amount);
        }
    }

    public record WithdrawFailedEvent(
            String sagaId,
            String accountNumber,
            String reason
    ) {
        public static TransferEvent.WithdrawFailedEvent insufficientBalance(String sagaId, String accountNumber) {
            return  new TransferEvent.WithdrawFailedEvent(sagaId, accountNumber, "Insufficient balance");
        }

        public static TransferEvent.WithdrawFailedEvent unknownError(String sagaId, String accountNumber, String message) {
            return  new TransferEvent.WithdrawFailedEvent(
                    sagaId,
                    accountNumber,
                    message != null ? message : "Unkown error"
            );
        }
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
