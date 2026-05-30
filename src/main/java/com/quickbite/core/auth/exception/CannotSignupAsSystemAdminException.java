package com.quickbite.core.auth.exception;

import com.quickbite.core.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CannotSignupAsSystemAdminException extends BaseException {
    public CannotSignupAsSystemAdminException() {
        super("You cannot register as a system admin", HttpStatus.UNAUTHORIZED);
    }
}
