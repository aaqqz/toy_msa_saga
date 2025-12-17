package com.saga.notification.domain;

import com.saga.common.dto.NotificationDto;
import com.saga.common.event.TransferEvent;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "notifications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @Column(name = "notification_id")
    private String notificationId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "saga_id")
    private String sagaId;

    @Column(name = "notification_type", nullable = false)
    private String notificationType;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    public static Notification created(String notificationId, NotificationDto.NotificationRequest request) {
        Notification entity = new Notification();
        entity.notificationId = notificationId;
        entity.userId = request.userId();
        entity.sagaId = request.sagaId();
        entity.notificationType = request.notificationType();
        entity.message = request.message();
        entity.status = "SENT";

        return entity;
    }

    public static Notification created(String notificationId, TransferEvent.DepositSuccessEvent event) {
        Notification entity = new Notification();
        entity.notificationId = notificationId;
        entity.userId = event.accountNumber();
        entity.sagaId = event.sagaId();
        entity.notificationType = "DEPOSIT_SUCCESS";
        entity.message = "u received " + event.amount();
        entity.status = "SENT";

        return entity;
    }

    public static Notification withdrawFailed(TransferEvent.WithdrawFailedEvent event) {
        Notification entity = new Notification();
        entity.notificationId = UUID.randomUUID().toString();
        entity.userId = event.accountNumber();
        entity.sagaId = event.sagaId();
        entity.notificationType = "WITHDRAW_FAILED";
        entity.message = "Withdraw failed " + event.reason();
        entity.status = "SENT";

        return entity;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = this.createdAt == null ? LocalDateTime.now() : this.createdAt;
    }


}
