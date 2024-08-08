package com.electric_titans.accountservice.service;

import com.electric_titans.accountservice.entity.User;
import com.electric_titans.accountservice.repository.UserRepository;
import com.electric_titans.accountservice.service.impl.TokenBlacklistServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TokenBlacklistServiceTest {

    @InjectMocks
    private TokenBlacklistServiceImpl tokenBlacklistService;

    @Mock
    private UserRepository userRepository;

    @Test
    void testIsBlacklistedNotBlacklistedUser() {
        // given
        boolean isNotBlacklisted = false;
        String email = "sample@email.com";
        User user = new User();
        user.setBlacklisted(isNotBlacklisted);
        user.setEmail("sample@email.com");
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // when
        boolean isValid = tokenBlacklistService.isBlacklisted("sample@email.com");

        // then
        Assertions.assertFalse(isValid);
    }

    @Test
    void testIsBlacklistedBlacklistedUser() {
        // given
        boolean isNotBlacklisted = true;
        String email = "sample@email.com";
        User user = new User();
        user.setBlacklisted(isNotBlacklisted);
        user.setEmail("sample@email.com");
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // when
        boolean isValid = tokenBlacklistService.isBlacklisted("sample@email.com");

        // then
        Assertions.assertTrue(isValid);
    }
}
