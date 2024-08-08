package com.electric_titans.paymentgatewayservice.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.nio.file.AccessDeniedException;
import java.security.SignatureException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(PaymentProcessingException.class)
    public ResponseEntity<ErrorResponse> handlePaymentProcessingException(PaymentProcessingException ex, WebRequest request) {
        logger.error("PaymentProcessingException: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
        @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(AuthorizationDeniedException exception, WebRequest webRequest) {
        logger.debug("handleAuthorizationDeniedException({}, {})", exception.getMessage(), webRequest.getContextPath());
        return buildErrorDetailsResponse(exception.getMessage(), webRequest.getDescription(false), HttpStatus.FORBIDDEN.value());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(WebRequest webRequest) {
        logger.debug("handleBadCredentialsException({})", webRequest.getContextPath());
        return buildErrorDetailsResponse("The username or password is incorrect", webRequest.getDescription(false), HttpStatus.UNAUTHORIZED.value());
    }

    @ExceptionHandler(AccountStatusException.class)
    public ResponseEntity<ErrorResponse> handleAccountStatusException(WebRequest webRequest) {
        logger.debug("handleAccountStatusException({})", webRequest.getContextPath());
        return buildErrorDetailsResponse("The account is locked", webRequest.getDescription(false), HttpStatus.FORBIDDEN.value());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(WebRequest webRequest) {
        logger.debug("handleAccessDeniedException({})", webRequest.getContextPath());
        return buildErrorDetailsResponse("You are not authorized to access this resource", webRequest.getDescription(false), HttpStatus.FORBIDDEN.value());
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorResponse> handleSignatureException(WebRequest webRequest) {
        logger.debug("handleSignatureException({})", webRequest.getContextPath());
        return buildErrorDetailsResponse("The JWT signature is invalid", webRequest.getDescription(false), HttpStatus.FORBIDDEN.value());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwtException(WebRequest webRequest) {
        logger.debug("handleExpiredJwtException({})", webRequest.getContextPath());
        return buildErrorDetailsResponse("The JWT token has expired", webRequest.getDescription(false), HttpStatus.FORBIDDEN.value());
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJwtException(MalformedJwtException exception, WebRequest webRequest) {
        logger.debug("handleMalformedJwtException({})", webRequest.getContextPath());
        return buildErrorDetailsResponse(exception.getMessage(), webRequest.getDescription(false), HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception exception, WebRequest webRequest) {
        return buildErrorDetailsResponse(exception.getMessage(), webRequest.getDescription(false), HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private ResponseEntity<ErrorResponse> buildErrorDetailsResponse(String message, String path, int statusCode) {
        logger.debug("buildErrorDetailsResponse({}, {}, {})", message, path, statusCode);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(message)
                .details(path)
                .status(statusCode)
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(statusCode));
    }
}