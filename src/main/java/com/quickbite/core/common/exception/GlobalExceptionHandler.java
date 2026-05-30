package com.quickbite.core.common.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String MDC_KEY = "correlationId";
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException ex) {
        return buildResponse(ex.getMessage(), ex.getStatus(), null);
    }

    // 1. Catch Validation Errors from JSON Request Bodies (@RequestBody)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> validationErrors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        logger.warn("Validation failed for request. Fields broken: {}", validationErrors.keySet());

        return buildResponse("Validation failed", HttpStatus.BAD_REQUEST, validationErrors);
    }

    // 2. Catch Validation Errors from URL Parameters / Query Strings (@PathVariable / @RequestParam)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> validationErrors = new HashMap<>();

        ex.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            // Optional: Extract only the last field name out of the path
            String fieldName = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
            validationErrors.put(fieldName, violation.getMessage());
        });

        logger.warn("Constraint violation triggered: {}", validationErrors.keySet());

        return buildResponse("Validation failed", HttpStatus.BAD_REQUEST, validationErrors);
    }

    // Handle unexpected system exceptions (Generic 500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        logger.error("Unhandled system exception encountered: ", ex);
        return buildResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR, null);
    }

    // Unified helper method handling optional validation payload details
    private ResponseEntity<ErrorResponse> buildResponse(String message, HttpStatus status, Map<String, String> errors) {
        ErrorResponse error = new ErrorResponse(
                message,
                status.value(),
                status.getReasonPhrase(),
                MDC.get(MDC_KEY),
                LocalDateTime.now(),
                errors
        );

        return new ResponseEntity<>(error, status);
    }
}