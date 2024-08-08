package com.electric_titans.userservice.service.impl;

import com.electric_titans.userservice.security.CustomUserDetails;
import com.electric_titans.userservice.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration-time}")
    private long jwtExpiration;

    @Override
    public String extractUsername(String token) {
        log.debug("extractUsername({})", token);
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        if (claimsResolver == null) {
            log.warn("Claims resolver is null");
            return null;
        }
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public String generateToken(CustomUserDetails userDetails) {
        log.debug("generateToken({})", userDetails.getEmail());
        return generateToken(new HashMap<>(), userDetails);
    }

    @Override
    public String generateToken(Map<String, Object> extraClaims, CustomUserDetails userDetails) {
        log.debug("generateToken({}, {})", extraClaims.size(), userDetails.getUsername());
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    @Override
    public long getExpirationTime() {
        log.debug("getExpirationTime()");
        return jwtExpiration;
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            CustomUserDetails userDetails,
            long expiration
    ) {
        log.debug("buildToken({}, {}, {})", extraClaims.size(), userDetails.getEmail(), expiration);
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public boolean isTokenValid(String token, CustomUserDetails userDetails) {
        final String username = extractUsername(token);
        log.info("Username: {}", username);
        return (username.equals(userDetails.getEmail())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String extractTokenFromRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}