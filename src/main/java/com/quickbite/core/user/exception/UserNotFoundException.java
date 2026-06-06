package com.quickbite.core.user.exception;

import com.quickbite.core.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseException {
    public UserNotFoundException() {
        super("User Not Found", HttpStatus.NOT_FOUND);
    }
}
