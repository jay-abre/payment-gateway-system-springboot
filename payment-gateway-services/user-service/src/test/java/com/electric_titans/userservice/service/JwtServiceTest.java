package com.electric_titans.userservice.service;

import com.electric_titans.userservice.security.CustomUserDetails;
import com.electric_titans.userservice.service.impl.JwtServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class JwtServiceTest {

    @InjectMocks
    private JwtServiceImpl jwtService;

    @Mock
    private CustomUserDetails customUserDetails;

    @Mock
    private HttpServletRequest request;

    @Value("${jwt.secret}")
    private String secretKey = "mysecretkey";

    @Value("${jwt.expiration-time}")
    private long jwtExpiration = 1000L * 60 * 60; // 1 hour

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testExtractTokenFromRequest() {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Bearer token");

        // When
        String token = jwtService.extractTokenFromRequest(request);

        // Then
        assertEquals("token", token);
    }

    private String generateToken(String username) {
        return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}