package com.quickbite.core.common.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        String message,
        int status,
        String error,
        String correlationId,
        LocalDateTime timestamp
) {
}
