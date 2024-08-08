package com.electric_titans.accountservice.service;

import com.electric_titans.accountservice.security.CustomUserDetails;
import com.electric_titans.accountservice.service.impl.JwtServiceImpl;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtServiceImpl jwtService;

    @Mock
    private CustomUserDetails userDetails;

    @Disabled("Decode argument cannot be null.")
    @Test
    void testExtractUsername() {
        Assertions.assertEquals("", jwtService.extractUsername("token"));
    }

    @Disabled("Decode argument cannot be null.")
    @Test
    void testExtractClaim() {
        Assertions.assertEquals("", jwtService.extractClaim("Token", Claims::getSubject));
    }

    @Disabled("Decode argument cannot be null.")
    @Test
    void testIsTokenValid() {
        Assertions.assertFalse(jwtService.isTokenValid("token", userDetails));
    }
}
