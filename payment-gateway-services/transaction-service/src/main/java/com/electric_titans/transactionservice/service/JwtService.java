package com.electric_titans.transactionservice.service;

import com.electric_titans.transactionservice.security.CustomUserDetails;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.function.Function;

public interface JwtService {
    String extractUsername(String token);
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
    boolean isTokenValid(String token, CustomUserDetails userDetails);
}