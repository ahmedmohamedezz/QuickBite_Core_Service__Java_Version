package com.quickbite.core.restaurant.exception;

import com.quickbite.core.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class RestaurantNotFoundException extends BaseException {
    public RestaurantNotFoundException() {
        super("Restaurant Not Found", HttpStatus.NOT_FOUND);
    }
}
