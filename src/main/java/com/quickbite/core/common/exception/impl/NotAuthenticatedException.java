package com.quickbite.core.common.exception.impl;

import com.quickbite.core.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class NotAuthenticatedException extends BaseException {
    public NotAuthenticatedException() {
        super("User Not Authenticated", HttpStatus.UNAUTHORIZED);
    }
}
