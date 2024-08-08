package com.electric_titans.userservice.controller;

import com.electric_titans.userservice.dto.response.UserResponse;
import com.electric_titans.userservice.dto.response.LoginResponse;
import com.electric_titans.userservice.dto.request.LoginRequest;
import com.electric_titans.userservice.dto.request.RegisterRequest;
import com.electric_titans.userservice.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Operation(
            summary = "Register a new user",
            description = "Registers a new user with the provided details."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input")
    })
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> register(@RequestBody @Valid RegisterRequest registerRequest) {
        log.debug("register({})", registerRequest.getEmail());
        return new ResponseEntity<>(authenticationService.signup(registerRequest), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Authenticate a user",
            description = "Authenticates a user and provides a login token."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully authenticated"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        log.debug("authenticated({})", loginRequest.getEmail());
        return ResponseEntity.ok(authenticationService.authenticate(loginRequest));
    }

    @Operation(
            summary = "Logout a user",
            description = "Logs out the currently authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully logged out"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated")
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest httpServletRequest) {
        log.debug("logout()");
        return ResponseEntity.ok(authenticationService.logout(httpServletRequest));
    }
}
