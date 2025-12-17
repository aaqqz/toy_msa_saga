package com.saga.notification.service;

import com.saga.common.dto.NotificationDto;
import com.saga.common.event.TransferEvent;
import com.saga.notification.domain.Notification;
import com.saga.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public NotificationDto.NotificationResponse sendNotification(NotificationDto.NotificationRequest request) {
        String notificationId = UUID.randomUUID().toString();

        // todo 생성자 목적별 refactoring or created 생성자내에서 type 으로 구분
        Notification notification = Notification.created(notificationId, request);
        notificationRepository.save(notification);

        // TODO -> 실제 알람을 전송 or 이메일 전송
        log.info("[NOTIFICATION] Type: {}, User: {}, Message: {}",
                request.notificationType(), request.userId(), request.message()
        );

        return new NotificationDto.NotificationResponse(notificationId, notification.getNotificationType());
    }

    @Transactional
    @KafkaListener(topics = "transaction.deposit.success", groupId = "notification-service-group")
    public void handleDepositSuccess(TransferEvent.DepositSuccessEvent event) {
        try {
            String notificationId = UUID.randomUUID().toString();

            Notification notification = Notification.created(notificationId, event);
            notificationRepository.save(notification);

            // TODO -> 실제 알람을 전송 or 이메일 전송
            log.info("[NOTIFICATION] Type: {}, User: {}, Message: {}",
                    notification.getNotificationType(), notification.getUserId(), notification.getMessage()
            );
        } catch (Exception e) {
            TransferEvent.NotificationFailedEvent notificationFailedEvent = new TransferEvent.NotificationFailedEvent(
                    event.sagaId(),
                    event.accountNumber(),
                    e.getMessage() != null ? e.getMessage() : "Notification processing failed"
            );
            kafkaTemplate.send("notification.failed", notificationFailedEvent);

            log.error("[NOTIFICATION] Failed to send deposit success notification: {}", e.getMessage());
        }
    }

    @Transactional
    @KafkaListener(topics = "account.withdraw.failed", groupId = "notification-service-group")
    public void handleWithdrawFailed(TransferEvent.WithdrawFailedEvent event) {
        Notification notification = Notification.withdrawFailed(event);

        notificationRepository.save(notification);

        log.info("[NOTIFICATION] Withdraw failed notification sent to {}", event.accountNumber());
    }
}
