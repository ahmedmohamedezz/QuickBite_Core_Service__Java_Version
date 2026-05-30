package com.quickbite.core.auth.exception;

import com.quickbite.core.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends BaseException {
    public UserAlreadyExistsException() {
        super("User Already Exists with same phone or email", HttpStatus.BAD_REQUEST);
    }
}
