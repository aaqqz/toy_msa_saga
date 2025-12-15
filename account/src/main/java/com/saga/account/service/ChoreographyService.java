package com.saga.account.service;

import com.saga.account.domain.Account;
import com.saga.account.domain.AccountTransaction;
import com.saga.account.domain.SagaState;
import com.saga.account.repository.AccountRepository;
import com.saga.account.repository.AccountTransactionRepository;
import com.saga.account.repository.SagaStateRepository;
import com.saga.common.dto.TransferDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.saga.common.event.TransferEvent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChoreographyService {

    private final AccountRepository accountRepository;
    private final AccountTransactionRepository accountTransactionRepository;
    private final SagaStateRepository sagaStateRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public TransferDto.TransferResponse executeTransfer(TransferDto.TransferRequest request) {
        String sagaId = UUID.randomUUID().toString();

        try {
            // 1, Withdraw from source account
            Account fromAccount = accountRepository.findByAccountNumber(request.fromAccountNumber())
                    .orElseThrow(() -> new IllegalArgumentException("Source account not found"));

            if (fromAccount.getBalance().compareTo(request.amount()) < 0) {
                WithdrawFailedEvent failedEvent = WithdrawFailedEvent.insufficientBalance(sagaId, fromAccount.getAccountNumber());
                kafkaTemplate.send("account.withdraw.failed", failedEvent);
                return TransferDto.TransferResponse.insufficientBalance(sagaId);
            }

            fromAccount.subtractBalance(request.amount());
            accountRepository.save(fromAccount);

            AccountTransaction withdrawTx = AccountTransaction.withdraw(sagaId, request, fromAccount);
            accountTransactionRepository.save(withdrawTx);

            SagaState sagaState = SagaState.started(sagaId, request, fromAccount);
            sagaStateRepository.save(sagaState);

            WithdrawSuccessEvent successEvent = WithdrawSuccessEvent.success(sagaId, request.fromAccountNumber(), request.toAccountNumber(), request.amount());
            kafkaTemplate.send("account.withdraw.success", successEvent);

            return TransferDto.TransferResponse.completed(sagaId);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            WithdrawFailedEvent event = WithdrawFailedEvent.unknownError(sagaId, request.toAccountNumber(), e.getMessage());
            kafkaTemplate.send("account.withdraw.failed", event);
            return TransferDto.TransferResponse.error(sagaId, e.getMessage());
        }
    }

    @KafkaListener(topics = "transaction.deposit.failed", groupId = "account-service-group")
    public void handleDepositFailed(DepositFailedEvent event) {
        compensateWithdraw(event.sagaId()); // todo proxy 를 통한 @Transaction 사용 필요
    }

    @Transactional
    public void compensateWithdraw(String sagaId) {
        SagaState sagaState = sagaStateRepository.findById(sagaId)
                .orElse(null);
        if (sagaState == null) {
            return;
        }

        Account fromAccount = accountRepository.findById(sagaState.getFromAccountId())
                .orElse(null);
        if (fromAccount == null) {
            return;
        }

        fromAccount.addBalance(sagaState.getAmount());
        accountRepository.save(fromAccount);

        sagaState.compensated();
        sagaStateRepository.save(sagaState);
    }

    @KafkaListener(topics = "transaction.deposit.success", groupId = "account-service-group")
    public void handleDepositSuccess(DepositSuccessEvent event) {
        sagaStateRepository.findById(event.sagaId())
                .ifPresent(it -> {
                    it.completed();
                    sagaStateRepository.save(it);
                });
    }

    @KafkaListener(topics = "notofication.failed", groupId = "account-service-group")
    public void handleNotificationFailed(NotificationFailedEvent event) {
        log.info("[SAGA] Notification failed for saga {}: {}", event.sagaId(), event.reason());
        sagaStateRepository.findById(event.sagaId())
                .ifPresent(it -> {
                    it.notificationFailed();
                    sagaStateRepository.save(it);
                });

    }
}
