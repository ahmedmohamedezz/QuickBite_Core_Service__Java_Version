package com.quickbite.core.auth.exception;

import com.quickbite.core.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidOTPException extends BaseException {
    public InvalidOTPException() {
        super("Invalid OTP", HttpStatus.UNAUTHORIZED);
    }
}
