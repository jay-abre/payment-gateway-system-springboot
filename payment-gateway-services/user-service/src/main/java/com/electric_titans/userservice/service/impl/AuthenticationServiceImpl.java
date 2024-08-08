package com.electric_titans.userservice.service.impl;

import com.electric_titans.common.event.UserCreatedEvent;
import com.electric_titans.userservice.dto.request.RegisterRequest;
import com.electric_titans.userservice.dto.response.LoginResponse;
import com.electric_titans.userservice.dto.request.LoginRequest;
import com.electric_titans.userservice.dto.response.UserResponse;
import com.electric_titans.userservice.entity.Role;
import com.electric_titans.userservice.entity.User;
import com.electric_titans.userservice.enums.RoleEnum;
import com.electric_titans.userservice.enums.UserStatusEnum;
import com.electric_titans.userservice.exception.ResourceNotFoundException;
import com.electric_titans.userservice.exception.StripeCustomerException;
import com.electric_titans.userservice.exception.TooManyRequestsException;
import com.electric_titans.userservice.exception.UserAlreadyExistsException;
import com.electric_titans.userservice.mapper.UserMapper;
import com.electric_titans.userservice.repository.RoleRepository;
import com.electric_titans.userservice.repository.UserRepository;
import com.electric_titans.userservice.service.AuthenticationService;
import com.electric_titans.userservice.service.JwtService;
import com.electric_titans.userservice.service.RateLimiterService;
import com.electric_titans.userservice.service.TokenBlacklistService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final RateLimiterService rateLimiterService;
    private final TokenBlacklistService tokenBlacklistService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Override
    public UserResponse signup(RegisterRequest registerRequest) {
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new UserAlreadyExistsException(registerRequest.getEmail());
        }

        Role role = roleRepository.findByName(RoleEnum.USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", RoleEnum.USER.name()));

        User user = UserMapper.INSTANCE.registerRequestToUser(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setStatus(UserStatusEnum.ACTIVE);
        user.setRole(role);
        user.setBlacklisted(true);
        String middleName = user.getMiddleName() == null ? "" : user.getMiddleName().concat(" ");
        String fullName = user.getFirstName().concat(" ").concat(middleName).concat(user.getLastName());
        Stripe.apiKey = stripeApiKey;
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setEmail(registerRequest.getEmail())
                .setName(fullName)
                .build();

        Customer customer;
        try {
            customer = Customer.create(params);
        } catch (StripeException e) {
            throw new StripeCustomerException("Failed to create customer with Stripe");
        }

        user.setCustomerId(customer.getId());
        log.info("Customer created with id: {}", customer.getId());
        User savedUser = userRepository.save(user);
        UserCreatedEvent userCreatedEvent = UserCreatedEvent.builder()
                .name(fullName)
                .email(savedUser.getEmail())
                .build();

        kafkaTemplate.send("USER-CREATED", userCreatedEvent);
        log.info("User registered with email: {}", savedUser.getEmail());
        return UserMapper.INSTANCE.userToUserResponse(savedUser);
    }

    @Override
    public LoginResponse authenticate(LoginRequest input) {
        if (!rateLimiterService.isRateLimited()) {
            throw new TooManyRequestsException("Too many requests");
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword()));
        User authenticatedUser = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", input.getEmail()));

        authenticatedUser.setBlacklisted(false);
        Instant currentDate = Instant.now();
        User updatedUser = userRepository.save(authenticatedUser);
        String jwtToken = jwtService.generateToken(updatedUser);
        return LoginResponse.builder()
                .userId(updatedUser.getId().toString())
                .token(jwtToken)
                .expiresAt(currentDate.plusMillis(jwtService.getExpirationTime()).toString())
                .build();
    }

    @Override
    public String logout(HttpServletRequest httpServletRequest) {
        String token = jwtService.extractTokenFromRequest(httpServletRequest);
        String userEmail = jwtService.extractUsername(token);
        tokenBlacklistService.addToBlacklist(userEmail);
        return "Logged out successfully";
    }
}
