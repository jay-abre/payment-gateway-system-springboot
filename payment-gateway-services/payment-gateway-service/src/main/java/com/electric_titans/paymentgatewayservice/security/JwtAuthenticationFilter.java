package com.electric_titans.paymentgatewayservice.security;

import com.electric_titans.paymentgatewayservice.entity.User;
import com.electric_titans.paymentgatewayservice.exception.ResourceNotFoundException;
import com.electric_titans.paymentgatewayservice.repository.UserRepository;
import com.electric_titans.paymentgatewayservice.service.JwtService;
import com.electric_titans.paymentgatewayservice.service.TokenBlacklistService;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService blacklistService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws
            ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("Authentication header : {}", authHeader);
            log.warn("Authentication header is null/non-bearer");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final String userEmail = jwtService.extractUsername(jwt);

            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();
            if (userEmail != null && authentication == null
                    && !blacklistService.isBlacklisted(userEmail)) {
                log.info("No existing authentication header");
                UserDetails userDetails =
                        this.userDetailsService.loadUserByUsername(userEmail);
                if (jwtService.isTokenValid(jwt, (CustomUserDetails) userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new
                            UsernamePasswordAuthenticationToken(userDetails,
                            null,
                            userDetails.getAuthorities());
                    authToken.setDetails(new
                            WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    log.warn("Token has expired");
                    User user = userRepository.findByEmail(userEmail)
                            .orElseThrow(() -> new ResourceNotFoundException("User",
                                    "email", userEmail));
                    user.setBlacklisted(true);
                    userRepository.save(user);
                }
            }

            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            handlerExceptionResolver.resolveException(request, response, null,
                    exception);
        }
    }
}
