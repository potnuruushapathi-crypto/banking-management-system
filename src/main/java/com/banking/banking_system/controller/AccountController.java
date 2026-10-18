package com.banking.banking_system.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.banking.banking_system.dto.AccountResponse;
import com.banking.banking_system.dto.AmountRequest;
import com.banking.banking_system.dto.CreateAccountRequest;
import com.banking.banking_system.service.AccountService;
import com.banking.banking_system.util.AuthUtil;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@AuthenticationPrincipal Jwt jwt,
                                                         @Valid @RequestBody CreateAccountRequest request) {
        AccountResponse created = accountService.createAccount(
                AuthUtil.customerId(jwt), request.customerId(), request.accountType());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccount(@AuthenticationPrincipal Jwt jwt,
                                                      @PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getAccount(AuthUtil.customerId(jwt), accountNumber));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AccountResponse>> getAccountsByCustomer(@AuthenticationPrincipal Jwt jwt,
                                                                       @PathVariable Long customerId) {
        return ResponseEntity.ok(accountService.getAccountsByCustomer(AuthUtil.customerId(jwt), customerId));
    }

    @PostMapping("/{accountNumber}/deposit")
    public ResponseEntity<AccountResponse> deposit(@AuthenticationPrincipal Jwt jwt,
                                                   @PathVariable String accountNumber,
                                                   @Valid @RequestBody AmountRequest request) {
        return ResponseEntity.ok(accountService.deposit(
                AuthUtil.customerId(jwt), accountNumber, request.amount()));
    }

    @PostMapping("/{accountNumber}/withdraw")
    public ResponseEntity<AccountResponse> withdraw(@AuthenticationPrincipal Jwt jwt,
                                                    @PathVariable String accountNumber,
                                                    @Valid @RequestBody AmountRequest request) {
        return ResponseEntity.ok(accountService.withdraw(
                AuthUtil.customerId(jwt), accountNumber, request.amount()));
    }
}