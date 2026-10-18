package com.banking.banking_system.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        String type,
        BigDecimal amount,
        BigDecimal balanceAfter,
        String relatedAccountNumber,
        String description,
        LocalDateTime createdAt) {
}