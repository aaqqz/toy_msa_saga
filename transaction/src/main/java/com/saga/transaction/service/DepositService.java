package com.saga.transaction.service;

import com.saga.common.dto.DepositDto;
import com.saga.common.dto.NotificationDto;
import com.saga.common.event.TransferEvent;
import com.saga.transaction.domain.Deposit;
import com.saga.transaction.domain.Transaction;
import com.saga.transaction.repository.DepositRepository;
import com.saga.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositService {

    private final TransactionRepository transactionRepository;
    private final DepositRepository depositRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final WebClient webClient;
    @Value("${service.notification.url}")
    private String notificationServiceUrl;

    @Transactional
    public DepositDto.DepositResponse processDeposit(DepositDto.DepositRequest request) {
        try {
            String transactionId = UUID.randomUUID().toString();
            String depositId = UUID.randomUUID().toString();

            Transaction transaction = Transaction.completed(transactionId, request);
            transactionRepository.save(transaction);

            Deposit deposit = Deposit.completed(depositId, transactionId, request);
            depositRepository.save(deposit);

            try {
                NotificationDto.NotificationRequest notificationRequest = NotificationDto.NotificationRequest.depositSuccess(request);

                webClient.post()
                        .uri(notificationServiceUrl + "/internal/notification")
                        .bodyValue(notificationRequest)
                        .retrieve()
                        .bodyToMono(NotificationDto.NotificationResponse.class)
                        .block();

            } catch (Exception e) {
                log.error("Error processing deposit request", e.getMessage(), e);
                // todo 데이터 유실이 어느정도 허용된다면 모든 상황에 보상 트랜젝션이 필요한 것은 아니다
            }

            return new DepositDto.DepositResponse(depositId, "COMPLETED");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional
    @KafkaListener(topics = "account.withdraw.success", groupId = "transaction-service-group")
    public void handleWithdrawSuccess(TransferEvent.WithdrawSuccessEvent event) {
        // 출금 성공 이벤트

        try {
            String transactionId = UUID.randomUUID().toString();
            String depositId = UUID.randomUUID().toString();

            Transaction transaction = Transaction.completed(transactionId, event);
            transactionRepository.save(transaction);

            Deposit deposit = Deposit.completed(depositId, transactionId, event);
            depositRepository.save(deposit);

            TransferEvent.DepositSuccessEvent depositSuccessEvent = new TransferEvent.DepositSuccessEvent(event.sagaId(), event.toAccountNumber(), event.amount());
            kafkaTemplate.send("transaction.deposit.success", depositSuccessEvent);
        } catch (Exception e) {
            // DLQ 패턴을 통해 보상 트랜잭션을 따로 구현해도 무방하다.
            log.error(e.getMessage(), e);
            TransferEvent.DepositFailedEvent depositSuccessEvent = new TransferEvent.DepositFailedEvent(
                    event.sagaId(),
                    event.toAccountNumber(),
                    e.getMessage() != null ? e.getMessage() : "Uknown error"
            );
            kafkaTemplate.send("transaction.deposit.failed", depositSuccessEvent);
            throw new RuntimeException(e.getMessage());
        }
    }

    @KafkaListener(topics = "notification.failed", groupId = "transaction-service-group")
    public void handleWithdrawFailed(TransferEvent.NotificationFailedEvent event) {
        log.error("[TRANSACTION] Notification failed for saga {}: {}", event.sagaId(), event.reason());
    }
}
