package com.quickbite.core.common.exception.impl;

import com.quickbite.core.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ServerException extends BaseException {
    public ServerException() {
        super("Something Went Wrong", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
