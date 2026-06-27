package com.quickbite.core.customer_addresses.exception;

import com.quickbite.core.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class CustomerAddressNotFoundException extends BaseException {
    public CustomerAddressNotFoundException() {
        super("Customer Address Not Found", HttpStatus.NOT_FOUND);
    }
}
