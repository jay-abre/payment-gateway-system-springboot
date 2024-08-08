package com.electric_titans.accountservice.exception;

import com.electric_titans.accountservice.dto.response.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;
import java.security.SignatureException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException exception,
                                                                         WebRequest webRequest) {
        log.debug("handleResourceNotFoundException({}, {})", exception.getMessage(), webRequest.getContextPath());
        return buildErrorDetailsResponse(exception.getMessage(), webRequest.getDescription(false), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.debug("handleMethodArgumentNotValidException({})", exception.getMessage());
        Map<String, List<String>> errors = new HashMap<>();
        List<String> errorMessages = exception.getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        errors.put("errors", errorMessages);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(WebRequest webRequest) {
        log.debug("handleHttpMessageNotReadableException({})", webRequest.getContextPath());
        return buildErrorDetailsResponse("There was an issue processing your request. " +
                        "One of the values provided is not recognized. " +
                        "Please ensure all values are correct and try again.",
                webRequest.getDescription(false),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(KycNotFullyVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleKycNotFullyVerifiedException(KycNotFullyVerifiedException exception, WebRequest webRequest) {
        log.debug("handleKycNotFullyVerifiedException({}, {})", exception.getMessage(), webRequest.getContextPath());
        return buildErrorDetailsResponse(exception.getMessage(), webRequest.getDescription(false), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(WebRequest webRequest) {
        log.debug("handleBadCredentialsException({})", webRequest.getContextPath());
        return buildErrorDetailsResponse("The username or password is incorrect", webRequest.getDescription(false), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccountStatusException.class)
    public ResponseEntity<ErrorResponse> handleAccountStatusException(WebRequest webRequest) {
        log.debug("handleAccountStatusException({})", webRequest.getContextPath());
        return buildErrorDetailsResponse("The account is locked", webRequest.getDescription(false), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(WebRequest webRequest) {
        log.debug("handleAccessDeniedException({})", webRequest.getContextPath());
        return buildErrorDetailsResponse("You are not authorized to access this resource", webRequest.getDescription(false), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorResponse> handleSignatureException(WebRequest webRequest) {
        log.debug("handleSignatureException({})", webRequest.getContextPath());
        return buildErrorDetailsResponse("The JWT signature is invalid", webRequest.getDescription(false), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwtException(WebRequest webRequest) {
        log.debug("handleExpiredJwtException({})", webRequest.getContextPath());
        return buildErrorDetailsResponse("The JWT token has expired", webRequest.getDescription(false), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJwtException(MalformedJwtException exception, WebRequest webRequest) {
        log.debug("handleMalformedJwtException({})", webRequest.getContextPath());
        return buildErrorDetailsResponse(exception.getMessage(), webRequest.getDescription(false), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception exception, WebRequest webRequest) {
        log.debug("handleGlobalException({}, {})", exception.getMessage(), webRequest.getContextPath());
        return buildErrorDetailsResponse(exception.getMessage(), webRequest.getDescription(false), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> buildErrorDetailsResponse(String message, String path, HttpStatus httpStatus) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(Instant.now())
                .message(message)
                .path(path)
                .build();
        return new ResponseEntity<>(errorResponse, httpStatus);
    }
}
