package com.saga.common.dto;

public class NotificationDto {

    public record NotificationRequest(
            String sagaId,
            String userId,
            String notificationType,
            String message
    ) {
        public static NotificationRequest depositSuccess(DepositDto.DepositRequest request) {
            return new NotificationRequest(
                    request.sagaId(),
                    request.accountNumber(),
                    "DEPOSIT_SUCCESS",
                    "Received " + request.amount() + " from " + request.fromAccountNumber()
            );
        }
    }

    public record NotificationResponse(
            String notificationId,
            String status
    ) {
    }
}
