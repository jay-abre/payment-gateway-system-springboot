package com.electric_titans.userservice.service;

import com.electric_titans.userservice.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.function.Function;

public interface JwtService {

    String extractUsername(String token);

    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);

    String generateToken(CustomUserDetails userDetails);

    String generateToken(Map<String, Object> extraClaims, CustomUserDetails userDetails);

    long getExpirationTime();

    boolean isTokenValid(String token, CustomUserDetails userDetails);

    String extractTokenFromRequest(HttpServletRequest request);
}
