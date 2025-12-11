package com.saga.common.dto;

import java.math.BigDecimal;

public class DepositDto {

    public record DepositRequest(
            String sagaId,
            String accountNumber,
            BigDecimal amount,
            String fromAccountNumber
    ) {
    }

    public record DepositResponse(
            String depositId,
            String status
    ) {
    }
}
