package com.electric_titans.userservice.service;

import com.electric_titans.userservice.entity.User;
import com.electric_titans.userservice.exception.ResourceNotFoundException;
import com.electric_titans.userservice.repository.UserRepository;
import com.electric_titans.userservice.service.impl.TokenBlacklistServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

public class TokenBlacklistServiceTest {

    @InjectMocks
    private TokenBlacklistServiceImpl tokenBlacklistService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddToBlacklist_Success() {
        // Given
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);
        user.setBlacklisted(false);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        tokenBlacklistService.addToBlacklist(email);

        // Then
        assertTrue(user.isBlacklisted());
        verify(userRepository).findByEmail(email);
        verify(userRepository).save(user);
    }

    @Test
    public void testAddToBlacklist_UserNotFound() {
        // Given
        String email = "user@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            tokenBlacklistService.addToBlacklist(email);
        });

        assertEquals("User not found with email : " + email, exception.getMessage());
        verify(userRepository).findByEmail(email);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testIsBlacklisted_Success() {
        // Given
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);
        user.setBlacklisted(true);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // When
        boolean result = tokenBlacklistService.isBlacklisted(email);

        // Then
        assertTrue(result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    public void testIsBlacklisted_UserNotFound() {
        // Given
        String email = "user@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            tokenBlacklistService.isBlacklisted(email);
        });

        assertEquals("User not found with email : " + email, exception.getMessage());
        verify(userRepository).findByEmail(email);
    }
}
