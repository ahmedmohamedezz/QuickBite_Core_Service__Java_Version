package com.quickbite.core.rbac.exception;

import com.quickbite.core.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CannotAssignBranchesToOwnerException extends BaseException {
    public CannotAssignBranchesToOwnerException() {
        super("Cannot assign branches to owner", HttpStatus.BAD_REQUEST);
    }
}
