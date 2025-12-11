package com.saga.common.dto;

public class NotificationDto {

    public record NotificationRequest(
            String sagaId,
            String userId,
            String notificationType,
            String message
    ) {
    }

    public record NotificationResponse(
            String notificationId,
            String status
    ) {
    }
}
