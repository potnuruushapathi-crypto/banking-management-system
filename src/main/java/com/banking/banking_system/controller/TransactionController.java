package com.banking.banking_system.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banking.banking_system.dto.TransactionResponse;
import com.banking.banking_system.dto.TransferRequest;
import com.banking.banking_system.dto.TransferResponse;
import com.banking.banking_system.service.TransactionService;
import com.banking.banking_system.service.TransferService;
import com.banking.banking_system.util.AuthUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class TransactionController {

    private final TransferService transferService;
    private final TransactionService transactionService;

    public TransactionController(TransferService transferService,
                                 TransactionService transactionService) {
        this.transferService = transferService;
        this.transactionService = transactionService;
    }

    @PostMapping("/transfers")
    public ResponseEntity<TransferResponse> transfer(@AuthenticationPrincipal Jwt jwt,
                                                     @Valid @RequestBody TransferRequest request) {
        return ResponseEntity.ok(transferService.transfer(
                AuthUtil.customerId(jwt), request.fromAccount(), request.toAccount(), request.amount()));
    }

    @GetMapping("/accounts/{accountNumber}/transactions")
    public ResponseEntity<List<TransactionResponse>> history(@AuthenticationPrincipal Jwt jwt,
                                                             @PathVariable String accountNumber) {
        return ResponseEntity.ok(transactionService.getHistory(AuthUtil.customerId(jwt), accountNumber));
    }
}