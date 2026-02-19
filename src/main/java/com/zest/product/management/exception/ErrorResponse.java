package com.zest.product.management.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Custom error response object for centralized exception handling.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    @Builder.Default
    private String timestamp = LocalDateTime.now().toString();
    private int status;
    private String errorCode;
    private String message;
    private String path;
}
