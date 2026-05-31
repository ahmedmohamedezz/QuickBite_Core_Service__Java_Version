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
        StringBuilder error = new StringBuilder();

        ex.getBindingResult().getAllErrors().forEach((err) -> {
            String fieldName = ((FieldError) err).getField();
            String errorMessage = err.getDefaultMessage();
            error.append(
                    String.format("%s: %s", fieldName, errorMessage)
            );
        });

        logger.warn("Validation failed for request. Fields broken: {}", error.toString());

        return buildResponse("Validation failed", HttpStatus.BAD_REQUEST, error.toString());
    }

    // 2. Catch Validation Errors from URL Parameters / Query Strings (@PathVariable / @RequestParam)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        StringBuilder error = new StringBuilder();

        ex.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();

            // Optional: Extract only the last field name out of the path
            String fieldName = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
            error.append(
                    String.format("%s : %s", fieldName, violation.getMessage())
            );
        });

        logger.warn("Constraint violation triggered: {}", error.toString());

        return buildResponse("Validation failed", HttpStatus.BAD_REQUEST, error.toString());
    }

    // Handle unexpected system exceptions (Generic 500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        logger.error("Unhandled system exception encountered: ", ex);
        return buildResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR, null);
    }

    // Unified helper method handling optional validation payload details
    private ResponseEntity<ErrorResponse> buildResponse(String message, HttpStatus status, String error) {
        ErrorResponse errorResponse = new ErrorResponse(
                message,
                status.value(),
                error,
                MDC.get(MDC_KEY),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(errorResponse, status);
    }
}