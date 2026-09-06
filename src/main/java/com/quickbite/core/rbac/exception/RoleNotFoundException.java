package com.quickbite.core.rbac.exception;

import com.quickbite.core.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class RoleNotFoundException extends BaseException {
    public RoleNotFoundException() {
        super("Member role not found", HttpStatus.NOT_FOUND);
    }
}
