package com.saga.account.service;

import com.saga.account.domain.Account;
import com.saga.account.domain.AccountTransaction;
import com.saga.account.domain.SagaState;
import com.saga.account.repository.AccountRepository;
import com.saga.account.repository.AccountTransactionRepository;
import com.saga.account.repository.SagaStateRepository;
import com.saga.common.dto.DepositDto;
import com.saga.common.dto.TransferDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrchestrationService {

    private final AccountRepository accountRepository;
    private final AccountTransactionRepository accountTransactionRepository;
    private final SagaStateRepository sagaStateRepository;
    private final WebClient webClient;

    @Value("${service.transaction.url}")
    private String transactionServiceUrl;

    @Value("${service.notification.url}")
    private String notificationServiceUrl;

    @Transactional
    public TransferDto.TransferResponse executeTransfer(TransferDto.TransferRequest request) {
        String sagaId = UUID.randomUUID().toString();

        try {
            // 1, Withdraw from source account
            Account fromAccount = accountRepository.findByAccountNumber(request.fromAccountNumber())
                    .orElseThrow(() -> new IllegalArgumentException("Source account not found"));

            if (fromAccount.getBalance().compareTo(request.amount()) < 0) {
                return TransferDto.TransferResponse.insufficientBalance(sagaId);
            }

            fromAccount.subtractBalance(request.amount());
            accountRepository.save(fromAccount);

            AccountTransaction withdrawTx = AccountTransaction.withdraw(sagaId, request, fromAccount);
            accountTransactionRepository.save(withdrawTx);

            SagaState sagaState = SagaState.started(sagaId, request, fromAccount);
            sagaStateRepository.save(sagaState);

            try {
                DepositDto.DepositRequest depositRequest = new DepositDto.DepositRequest(
                        sagaId,
                        request.toAccountNumber(),
                        request.amount(),
                        request.fromAccountNumber()
                );

                DepositDto.DepositResponse response = webClient.post()
                        .uri(transactionServiceUrl + "/internal/deposit")
                        .bodyValue(depositRequest)
                        .retrieve()
                        .bodyToMono(DepositDto.DepositResponse.class)
                        .block(); // 동기
                if (response == null) {
                    throw new IllegalStateException("Deposit failed");
                }

                sagaState.completed();
                sagaStateRepository.save(sagaState);

                return TransferDto.TransferResponse.completed(sagaId);

            } catch (Exception e) {
                // 이후 서비스에 대한 보상 트랙잭션
                fromAccount.addBalance(request.amount());
                accountRepository.save(fromAccount);

                withdrawTx.compensated();
                accountTransactionRepository.save(withdrawTx);

                sagaState.compensated();
                sagaStateRepository.save(sagaState);

                log.error(e.getMessage(), e);
                return  TransferDto.TransferResponse.error(sagaId,
                        "Deposit failed: " + e.getMessage()
                );
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return  TransferDto.TransferResponse.error(sagaId, e.getMessage());
        }
    }
}
