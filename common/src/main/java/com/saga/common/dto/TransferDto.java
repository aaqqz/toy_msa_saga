package com.saga.common.dto;

import java.math.BigDecimal;

public class TransferDto {

    public record TransferRequest(
            String fromAccountNumber,
            String toAccountNumber,
            BigDecimal amount
    ) {
    }

    public record TransferResponse(
            String sagaId,
            String status,
            String message
    ) {

        public static TransferResponse completed(String sagaId) {
            return new TransferResponse(sagaId, "COMPLETED", "Transfer successful");
        }

        public static TransferResponse insufficientBalance(String sagaId) {
            return new TransferResponse(sagaId, "FAILED", "Insufficient Balance");
        }

        public static TransferResponse error(String sagaId, String message) {
            return new TransferResponse(
                    sagaId, "FAILED",
                    message != null ? message : "Unknown error"
            );
        }
    }

}
