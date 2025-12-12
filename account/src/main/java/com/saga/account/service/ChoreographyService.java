package com.saga.account.service;

import com.saga.account.repository.AccountRepository;
import com.saga.account.repository.AccountTransactionRepository;
import com.saga.account.repository.SagaStateRepository;
import com.saga.common.dto.TransferDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChoreographyService {

    private final AccountRepository accountRepository;
    private final AccountTransactionRepository accountTransactionRepository;
    private final SagaStateRepository sagaStateRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TransferDto.TransferResponse executeTransfer(TransferDto.TransferRequest request) {
        return null;
    }
}
