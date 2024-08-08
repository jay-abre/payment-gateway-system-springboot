package com.electric_titans.userservice.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StripeCustomerException extends RuntimeException {

    public StripeCustomerException(String message) {
        super(message);
        log.error(message);
    }

    public StripeCustomerException(String operation, String customerId) {
        super(String.format("Failed to %s stripe with customer id : %s", operation, customerId));
        log.error("Failed to {} stripe with customer id : {}", operation, customerId);
    }
}
