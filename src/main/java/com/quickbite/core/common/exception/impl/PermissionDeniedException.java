package com.quickbite.core.common.exception.impl;

import com.quickbite.core.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class PermissionDeniedException extends BaseException {
    public PermissionDeniedException() {
        super("Permission Denied", HttpStatus.FORBIDDEN);
    }
}
