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
    }

}
