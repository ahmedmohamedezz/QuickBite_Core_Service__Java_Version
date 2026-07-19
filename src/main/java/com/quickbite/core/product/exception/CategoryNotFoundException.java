package com.quickbite.core.product.exception;

import com.quickbite.core.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CategoryNotFoundException extends BaseException {
    public CategoryNotFoundException() {
        super("Category Not Found", HttpStatus.NOT_FOUND);
    }
}
