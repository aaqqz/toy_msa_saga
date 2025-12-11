package com.saga.account.service;

import com.saga.account.repository.AccountRepository;
import com.saga.account.repository.AccountTransactionRepository;
import com.saga.account.repository.SagaStateRepository;
import com.saga.common.dto.TransferDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class OrchestrationService {

    private final AccountRepository accountRepository;
    private final AccountTransactionRepository accountTransactionRepository;
    private final SagaStateRepository sagaStateRepository;
    private final WebClient webClient;

    @Value("{service.transaction.url}")
    private String transactionServiceUrl;

    @Value("{service.notification.url}")
    private String notificationServiceUrl;

    public TransferDto.TransferResponse executeTransfer(TransferDto.TransferRequest request) {


        return null;
    }
}
