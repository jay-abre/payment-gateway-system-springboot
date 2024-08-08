package com.electric_titans.userservice.controller;

import com.electric_titans.userservice.dto.request.LoginRequest;
import com.electric_titans.userservice.dto.request.RegisterRequest;
import com.electric_titans.userservice.dto.response.LoginResponse;
import com.electric_titans.userservice.dto.response.UserResponse;
import com.electric_titans.userservice.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegister() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("user@example.com");

        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);

        when(authenticationService.signup(registerRequest)).thenReturn(userResponse);

        // When
        ResponseEntity<UserResponse> responseEntity = authenticationController.register(registerRequest);

        // Then
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(userResponse, responseEntity.getBody());
    }

    @Test
    public void testLogin() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("user@example.com");
        loginRequest.setPassword("password");

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken("some-token");

        when(authenticationService.authenticate(loginRequest)).thenReturn(loginResponse);

        // When
        ResponseEntity<LoginResponse> responseEntity = authenticationController.login(loginRequest);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(loginResponse, responseEntity.getBody());
    }

    @Test
    public void testLogout() {
        // Given
        HttpServletRequest request = mock(HttpServletRequest.class);
        String logoutMessage = "User logged out successfully";

        when(authenticationService.logout(request)).thenReturn(logoutMessage);

        // When
        ResponseEntity<String> responseEntity = authenticationController.logout(request);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(logoutMessage, responseEntity.getBody());
    }
}