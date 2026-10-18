package com.banking.banking_system.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.banking.banking_system.dto.AccountResponse;
import com.banking.banking_system.entity.Account;
import com.banking.banking_system.entity.AccountTransaction;
import com.banking.banking_system.entity.Customer;
import com.banking.banking_system.exception.ForbiddenException;
import com.banking.banking_system.exception.InsufficientBalanceException;
import com.banking.banking_system.exception.ResourceNotFoundException;
import com.banking.banking_system.repository.AccountRepository;
import com.banking.banking_system.repository.CustomerRepository;
import com.banking.banking_system.repository.TransactionRepository;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository,
                          CustomerRepository customerRepository,
                          TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public AccountResponse createAccount(Long callerId, Long customerId, String accountType) {
        if (!callerId.equals(customerId)) {
            throw new ForbiddenException("You can only create accounts for yourself");
        }
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Account account = new Account();
        account.setCustomer(customer);
        account.setAccountType(accountType);
        account.setBalance(BigDecimal.ZERO);
        account.setAccountNumber(generateAccountNumber());

        return toResponse(accountRepository.save(account));
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccount(Long callerId, String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        checkOwner(account, callerId);
        return toResponse(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAccountsByCustomer(Long callerId, Long customerId) {
        if (!callerId.equals(customerId)) {
            throw new ForbiddenException("You can only view your own accounts");
        }
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found");
        }
        return accountRepository.findByCustomerId(customerId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AccountResponse deposit(Long callerId, String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findForUpdate(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        checkOwner(account, callerId);

        account.setBalance(account.getBalance().add(amount));
        Account saved = accountRepository.save(account);

        transactionRepository.save(AccountTransaction.of(
                saved, "DEPOSIT", amount, saved.getBalance(), null, "Cash deposit"));

        return toResponse(saved);
    }

    @Transactional
    public AccountResponse withdraw(Long callerId, String accountNumber, BigDecimal amount) {
        Account account = accountRepository.findForUpdate(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));
        checkOwner(account, callerId);

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(amount));
        Account saved = accountRepository.save(account);

        transactionRepository.save(AccountTransaction.of(
                saved, "WITHDRAW", amount, saved.getBalance(), null, "Cash withdrawal"));

        return toResponse(saved);
    }

    private void checkOwner(Account account, Long callerId) {
        if (!account.getCustomer().getId().equals(callerId)) {
            throw new ForbiddenException("This account does not belong to you");
        }
    }

    private String generateAccountNumber() {
        String number;
        do {
            number = String.valueOf(ThreadLocalRandom.current()
                    .nextLong(100000000000L, 1000000000000L));
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }

    private AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getBalance(),
                account.getCustomer().getId(),
                account.getCustomer().getName());
    }
}