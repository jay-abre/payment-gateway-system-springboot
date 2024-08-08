package com.electric_titans.bankreconciliationservice.exception;

import com.electric_titans.bankreconciliationservice.exception.ResourceNotFoundException;
import com.electric_titans.bankreconciliationservice.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException exception,
                                                                         WebRequest webRequest) {
        log.debug("handleResourceNotFoundException({}, {})", exception.getMessage(), webRequest.getContextPath());
        return buildErrorDetailsResponse(exception.getMessage(), webRequest.getDescription(false), HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<ErrorResponse> buildErrorDetailsResponse(String message, String path, HttpStatus statusCode) {
        log.debug("buildErrorDetailsResponse({}, {}, {})", message, path, statusCode);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .message(message)
                .path(path)
                .build();
        return new ResponseEntity<>(errorResponse, statusCode);
    }
}