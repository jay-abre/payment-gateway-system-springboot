package com.electric_titans.paymentgatewayservice.service;

import com.electric_titans.paymentgatewayservice.security.CustomUserDetails;
import io.jsonwebtoken.Claims;

import java.util.function.Function;

public interface JwtService {
    String extractUsername(String token);
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
    boolean isTokenValid(String token, CustomUserDetails userDetails);
}