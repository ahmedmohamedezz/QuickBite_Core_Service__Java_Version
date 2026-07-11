package com.quickbite.core.restaurant.exception;

import com.quickbite.core.common.exception.BaseException;
import org.springframework.http.HttpStatus;

public class RestaurantDataRequiredException extends BaseException {
    public RestaurantDataRequiredException() {
        super("Restaurant Data is Missing", HttpStatus.BAD_REQUEST);
    }
}
