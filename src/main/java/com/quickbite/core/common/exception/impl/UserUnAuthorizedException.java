package com.quickbite.core.common.exception.impl;

import com.quickbite.core.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class UserUnAuthorizedException extends BaseException {
    public UserUnAuthorizedException() {
        super("User Not Authorized", HttpStatus.UNAUTHORIZED);
    }
}
