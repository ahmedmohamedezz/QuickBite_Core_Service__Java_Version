package com.quickbite.core.retstaurant_branches.exception;

import com.quickbite.core.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class BranchNotFoundException extends BaseException {
    public BranchNotFoundException() {
        super("Restaurant Branch Not Found", HttpStatus.NOT_FOUND);
    }
}
