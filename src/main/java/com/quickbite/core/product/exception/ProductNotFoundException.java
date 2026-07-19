package com.quickbite.core.product.exception;

import com.quickbite.core.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends BaseException {
    public ProductNotFoundException() {
        super("Product Not Found", HttpStatus.NOT_FOUND);
    }
}
