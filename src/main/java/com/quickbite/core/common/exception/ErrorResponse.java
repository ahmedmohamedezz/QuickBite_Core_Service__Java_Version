package com.quickbite.core.common.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        String message,
        int status,
        String error,
        String correlationId,
        LocalDateTime timestamp,
        Map<String, String> errors
) {
    public ErrorResponse(String message, int status, String error, String correlationId, LocalDateTime timestamp) {
        this(message, status, error, correlationId, timestamp, null);
    }
}
