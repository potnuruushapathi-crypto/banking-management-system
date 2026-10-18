package com.banking.banking_system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.banking.banking_system.entity.AccountTransaction;

public interface TransactionRepository extends JpaRepository<AccountTransaction, Long> {

    List<AccountTransaction> findByAccountAccountNumberOrderByIdDesc(String accountNumber);
}