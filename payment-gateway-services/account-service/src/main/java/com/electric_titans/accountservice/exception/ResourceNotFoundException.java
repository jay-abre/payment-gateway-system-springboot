package com.electric_titans.accountservice.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, String fieldName, String fieldValue) {
        super(String.format("%s not found with %s : %s", resourceName, fieldName, fieldValue));
        log.error("{} not found with {} : {}", resourceName, fieldName, fieldValue);
    }
}
