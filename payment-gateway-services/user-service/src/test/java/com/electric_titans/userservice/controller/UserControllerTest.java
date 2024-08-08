package com.electric_titans.userservice.controller;

import com.electric_titans.userservice.dto.request.RegisterRequest;
import com.electric_titans.userservice.dto.request.UserProfileRequest;
import com.electric_titans.userservice.dto.response.UserResponse;
import com.electric_titans.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAuthenticatedUser() {
        // Given
        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);

        when(userService.getAuthenticatedUser()).thenReturn(userResponse);

        // When
        ResponseEntity<UserResponse> responseEntity = userController.getAuthenticatedUser();

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userResponse, responseEntity.getBody());
    }

    @Test
    public void testUpdateUser() {
        // Given
        Long userId = 1L;
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("user@example.com");

        UserResponse userResponse = new UserResponse();
        userResponse.setId(userId);

        when(userService.updateUser(userId, registerRequest)).thenReturn(userResponse);

        // When
        ResponseEntity<UserResponse> responseEntity = userController.updateUser(userId, registerRequest);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userResponse, responseEntity.getBody());
    }

    @Test
    public void testUpdateContactInformation() {
        // Given
        Long userId = 1L;
        MultipartFile idPicture = new MockMultipartFile("idPicture", "test.jpg", "image/jpeg", new byte[0]);
        UserProfileRequest userProfileRequest = new UserProfileRequest();

        UserResponse userResponse = new UserResponse();
        userResponse.setId(userId);

        when(userService.updateContactInformation(userId, userProfileRequest, idPicture)).thenReturn(userResponse);

        // When
        ResponseEntity<UserResponse> responseEntity = userController.updateContactInformation(userId, idPicture, userProfileRequest);

        // Then
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(userResponse, responseEntity.getBody());
    }
}