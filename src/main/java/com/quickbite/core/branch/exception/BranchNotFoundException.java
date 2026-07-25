package com.quickbite.core.branch.exception;

import com.quickbite.core.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class BranchNotFoundException extends BaseException {
    public BranchNotFoundException() {
        super("Restaurant Branch Not Found", HttpStatus.NOT_FOUND);
    }
}
