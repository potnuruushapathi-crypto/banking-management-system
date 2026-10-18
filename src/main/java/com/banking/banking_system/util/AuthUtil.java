package com.banking.banking_system.util;

import org.springframework.security.oauth2.jwt.Jwt;

public final class AuthUtil {

    private AuthUtil() {
    }

    public static Long customerId(Jwt jwt) {
        Object claim = jwt.getClaim("customerId");
        if (claim instanceof Number number) {
            return number.longValue();
        }
        throw new IllegalStateException("Token has no customerId");
    }
}