package com.banking.banking_system.dto;

import java.math.BigDecimal;

public record AccountResponse(
        Long id,
        String accountNumber,
        String accountType,
        BigDecimal balance,
        Long customerId,
        String customerName) {
}