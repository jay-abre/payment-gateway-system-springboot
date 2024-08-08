package com.electric_titans.userservice.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImageProcessingException extends RuntimeException {

    public ImageProcessingException(String message) {
        super(message);
    }
}
