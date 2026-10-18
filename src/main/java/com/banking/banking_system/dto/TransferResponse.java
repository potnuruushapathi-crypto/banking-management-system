package com.banking.banking_system.dto;

import java.math.BigDecimal;

public record TransferResponse(
        String fromAccount,
        String toAccount,
        BigDecimal amount,
        BigDecimal fromBalanceAfter) {
}