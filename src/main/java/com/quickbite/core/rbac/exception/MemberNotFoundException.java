package com.quickbite.core.rbac.exception;

import com.quickbite.core.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class MemberNotFoundException extends BaseException {
    public MemberNotFoundException() {
        super("Member not found", HttpStatus.NOT_FOUND);
    }
}
