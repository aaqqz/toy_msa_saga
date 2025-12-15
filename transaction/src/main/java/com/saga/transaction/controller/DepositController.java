package com.saga.transaction.controller;

import com.saga.common.dto.DepositDto;
import com.saga.transaction.service.DepositService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class DepositController {

    private final DepositService depositService;

    // todo account project 를 통해서만 진입 해야함, ip 제한 등 필요
    @PostMapping("/deposit")
    public DepositDto.DepositResponse processDeposit(@RequestBody DepositDto.DepositRequest request) {
        return depositService.processDeposit(request);
    }
}
