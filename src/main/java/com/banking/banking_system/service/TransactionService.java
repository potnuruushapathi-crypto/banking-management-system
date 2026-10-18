package com.banking.banking_system.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banking.banking_system.dto.TransactionResponse;
import com.banking.banking_system.entity.Account;
import com.banking.banking_system.exception.ForbiddenException;
import com.banking.banking_system.exception.ResourceNotFoundException;
import com.banking.banking_system.repository.AccountRepository;
import com.banking.banking_system.repository.TransactionRepository;

@Service
public class TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransactionService(AccountRepository accountRepository,
                              TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getHistory(Long callerId, String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!account.getCustomer().getId().equals(callerId)) {
            throw new ForbiddenException("This account does not belong to you");
        }

        return transactionRepository.findByAccountAccountNumberOrderByIdDesc(accountNumber).stream()
                .map(t -> new TransactionResponse(
                        t.getId(),
                        t.getType(),
                        t.getAmount(),
                        t.getBalanceAfter(),
                        t.getRelatedAccountNumber(),
                        t.getDescription(),
                        t.getCreatedAt()))
                .toList();
    }
}