package com.electric_titans.userservice.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String email) {
        super(String.format("User already exists with email: %s", email));
        log.error("User already exists with email: {}", email);
    }
}
