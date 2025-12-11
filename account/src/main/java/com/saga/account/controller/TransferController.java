package com.saga.account.controller;

import com.saga.account.service.ChoreographyService;
import com.saga.account.service.OrchestrationService;
import com.saga.common.dto.TransferDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TransferController {

    private final OrchestrationService orchestrationService;
    private final ChoreographyService choreographyService;

    @PostMapping("/orchestration/transfer")
    public TransferDto.TransferResponse orchestrationTransfer(@RequestBody TransferDto.TransferRequest request) {
        return orchestrationService.executeTransfer(request);
    }

    @PostMapping("/choreography/transfer")
    public TransferDto.TransferResponse choreographyTransfer(@RequestBody TransferDto.TransferRequest request) {
        return choreographyService.executeTransfer(request);
    }
}
