package com.electric_titans.userservice.controller;

import com.electric_titans.userservice.dto.request.RegisterRequest;
import com.electric_titans.userservice.dto.request.VerifyKycRequest;
import com.electric_titans.userservice.dto.response.StatusResponse;
import com.electric_titans.userservice.dto.response.UserPageResponse;
import com.electric_titans.userservice.dto.response.UserResponse;
import com.electric_titans.userservice.enums.KycStatusEnum;
import com.electric_titans.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringJUnitConfig
public class AdminControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateAdministrator() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("admin@example.com");

        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);


        when(userService.createAdministrator(registerRequest)).thenReturn(userResponse);

        // When
        ResponseEntity<UserResponse> responseEntity = adminController.createAdministrator(registerRequest);

        // Then
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    public void testGetAllUsers() {
        // Given
        UserPageResponse userPageResponse = new UserPageResponse();
        when(userService.getAllUsers(0, 10, "id", "asc", null, null)).thenReturn(userPageResponse);

        // When
        ResponseEntity<UserPageResponse> responseEntity = adminController.getAllUsers(0, 10, "id", "asc", null, null);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testGetUserById() {
        // Given
        Long userId = 1L;
        UserResponse userResponse = new UserResponse();
        userResponse.setId(userId);
        when(userService.getUserById(userId)).thenReturn(userResponse);

        // When
        ResponseEntity<UserResponse> responseEntity = adminController.getUserById(userId);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testDeactivateUser() {
        // Given
        Long userId = 1L;

        // When
        ResponseEntity<Void> responseEntity = adminController.deactivateUser(userId);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    @Test
    public void testCheckStatusByUserId() {
        // Given
        Long userId = 1L;
        StatusResponse statusResponse = new StatusResponse();
        when(userService.checkStatusByUserId(userId)).thenReturn(statusResponse);

        // When
        ResponseEntity<StatusResponse> responseEntity = adminController.checkStatusByUserId(userId);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    public void testVerifyUserKyc() {
        // Given
        Long userId = 1L;
        VerifyKycRequest verifyKycRequest = new VerifyKycRequest();
        verifyKycRequest.setKycStatusEnum(KycStatusEnum.FULLY_VERIFIED);

        UserResponse userResponse = new UserResponse();
        userResponse.setId(userId);
        when(userService.verifyUserKyc(userId, verifyKycRequest)).thenReturn(userResponse);

        // When
        ResponseEntity<UserResponse> responseEntity = adminController.verifyUserKyc(userId, verifyKycRequest);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }
}
