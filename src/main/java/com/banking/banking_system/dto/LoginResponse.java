package com.banking.banking_system.dto;

public record LoginResponse(
        String token,
        String tokenType,
        Long customerId,
        String name,
        String email) {
}