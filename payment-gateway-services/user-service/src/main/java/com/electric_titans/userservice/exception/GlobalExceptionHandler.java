package com.electric_titans.userservice.exception;

import com.electric_titans.userservice.dto.response.ErrorResponse;
import com.stripe.exception.StripeException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;

import java.nio.file.AccessDeniedException;
import java.security.SignatureException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException exception,
                                                                          WebRequest webRequest) {
        log.debug("handleUserAlreadyExistsException({}, {})", exception, webRequest);
        return buildErrorDetailsResponse(exception.getMessage(), webRequest.getDescription(false), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException exception,
                                                                         WebRequest webRequest) {
        log.debug("handleResourceNotFoundException({}, {})", exception, webRequest);
        return buildErrorDetailsResponse(exception.getMessage(), webRequest.getDescription(false), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ErrorResponse> handleTooManyRequestsException(TooManyRequestsException exception,
                                                                        WebRequest webRequest) {
        log.debug("handleTooManyRequestsException({}, {})", exception, webRequest);
        return buildErrorDetailsResponse(exception.getMessage(), webRequest.getDescription(false), HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ErrorResponse> handleMultipartException(MultipartException exception, WebRequest webRequest) {
        log.debug("handleFileUploadingError({}, {})", exception.getMessage(), webRequest.getContextPath());
        return buildErrorDetailsResponse(exception.getMessage(), webRequest.getDescription(false), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StripeException.class)
    public ResponseEntity<ErrorResponse> handleStripeCustomerException(StripeCustomerException exception, WebRequest webRequest) {
        log.debug("handleStripeCustomerException({}, {})", exception.getMessage(), webRequest.getContextPath());
        return buildErrorDetailsResponse(exception.getMessage(), webRequest.getDescription(false), HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(ImageProcessingException.class)
    public ResponseEntity<ErrorResponse> handleImageProcessingException(ImageProcessingException exception, WebRequest webRequest) {
        log.debug("handleImageProcessingException({}, {})", exception.getMessage(), webRequest.getContextPath());
        return buildErrorDetailsResponse(exception.getMessage(), webRequest.getDescription(false), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.debug("handleMethodArgumentNotValidException({})", exception);
        Map<String, List<String>> errors = new HashMap<>();
        List<String> errorMessages = exception.getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        errors.put("errors", errorMessages);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(AuthorizationDeniedException exception, WebRequest webRequest) {
        log.debug("handleAuthorizationDeniedException({}, {})", exception.getMessage(), webRequest.getContextPath());
        return buildErrorDetailsResponse(exception.getMessage(), webRequest.getDescription(false), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(WebRequest webRequest) {
        log.debug("handleHttpMessageNotReadableException({})", webRequest.getContextPath());
        return buildErrorDetailsResponse("There was an issue processing your request. " +
                "One of the values provided is not recognized. " +
                "Please ensure all values are correct and try again.",
                webRequest.getDescription(false),
                HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<ErrorResponse> handleGenericException(Exception exception, WebRequest webRequest) {
        return buildErrorDetailsResponse(exception.getMessage(), webRequest.getDescription(false), HttpStatus.INTERNAL_SERVER_ERROR);
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
