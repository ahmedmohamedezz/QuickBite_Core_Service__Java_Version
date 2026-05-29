package com.quickbite.core.common.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        int status,
        String error,
        String correlationId,
        LocalDateTime timestamp
) {
}
