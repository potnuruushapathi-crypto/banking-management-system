package com.banking.banking_system.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banking.banking_system.dto.TransferResponse;
import com.banking.banking_system.entity.Account;
import com.banking.banking_system.entity.AccountTransaction;
import com.banking.banking_system.exception.ForbiddenException;
import com.banking.banking_system.exception.InsufficientBalanceException;
import com.banking.banking_system.exception.InvalidRequestException;
import com.banking.banking_system.exception.ResourceNotFoundException;
import com.banking.banking_system.repository.AccountRepository;
import com.banking.banking_system.repository.TransactionRepository;

@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public TransferService(AccountRepository accountRepository,
                           TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransferResponse transfer(Long callerId, String fromNumber, String toNumber, BigDecimal amount) {
        if (fromNumber.equals(toNumber)) {
            throw new InvalidRequestException("Cannot transfer to the same account");
        }

        // Always lock accounts in the same order, so two opposite transfers cannot deadlock
        String firstNumber = fromNumber.compareTo(toNumber) < 0 ? fromNumber : toNumber;
        String secondNumber = firstNumber.equals(fromNumber) ? toNumber : fromNumber;

        Account first = lockAccount(firstNumber);
        Account second = lockAccount(secondNumber);

        Account source = first.getAccountNumber().equals(fromNumber) ? first : second;
        Account target = (source == first) ? second : first;

        if (!source.getCustomer().getId().equals(callerId)) {
            throw new ForbiddenException("The source account does not belong to you");
        }

        if (source.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        source.setBalance(source.getBalance().subtract(amount));
        target.setBalance(target.getBalance().add(amount));
        accountRepository.save(source);
        accountRepository.save(target);

        transactionRepository.save(AccountTransaction.of(
                source, "TRANSFER_OUT", amount, source.getBalance(), toNumber,
                "Transfer to " + toNumber));
        transactionRepository.save(AccountTransaction.of(
                target, "TRANSFER_IN", amount, target.getBalance(), fromNumber,
                "Transfer from " + fromNumber));

        return new TransferResponse(fromNumber, toNumber, amount, source.getBalance());
    }

    private Account lockAccount(String accountNumber) {
        return accountRepository.findForUpdate(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + accountNumber));
    }
}