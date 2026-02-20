package com.zest.product.management.dto;

public record TokenRefreshResponse(String token, String refreshToken, String tokenType) {
    public TokenRefreshResponse(String token, String refreshToken) {
        this(token, refreshToken, "Bearer");
    }
}
