package com.quickbite.core.rbac.exception;

import com.quickbite.core.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CannotCreateOwnerUserException extends BaseException {
    public CannotCreateOwnerUserException() {
        super("Cannot create another restaurant owner", HttpStatus.BAD_REQUEST);
    }
}
