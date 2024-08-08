package com.electric_titans.userservice.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.electric_titans.common.event.UserCreatedEvent;
import com.electric_titans.userservice.dto.request.RegisterRequest;
import com.electric_titans.userservice.dto.response.UserResponse;
import com.electric_titans.userservice.entity.Role;
import com.electric_titans.userservice.entity.User;
import com.electric_titans.userservice.enums.RoleEnum;
import com.electric_titans.userservice.repository.RoleRepository;
import com.electric_titans.userservice.repository.UserRepository;
import com.electric_titans.userservice.service.impl.AuthenticationServiceImpl;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

public class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set stripeApiKey directly for testing
        ReflectionTestUtils.setField(authenticationService, "stripeApiKey", "test-stripe-api-key");
    }

    @Test
    public void testSignup() throws StripeException {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("user@example.com");
        registerRequest.setPassword("password");
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");

        Role role = new Role();
        role.setName(RoleEnum.USER);

        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword(passwordEncoder.encode("password"));

        Customer customer = mock(Customer.class);
        when(customer.getId()).thenReturn("customer-id");

        // Mock Stripe Customer.create
        try (MockedStatic<Customer> mockedCustomer = mockStatic(Customer.class)) {
            mockedCustomer.when(() -> Customer.create(any(CustomerCreateParams.class))).thenReturn(customer);

            when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
            when(roleRepository.findByName(RoleEnum.USER)).thenReturn(Optional.of(role));
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(kafkaTemplate.send(anyString(), any())).thenReturn(null);

            // When
            UserResponse response = authenticationService.signup(registerRequest);

            // Then
            assertNotNull(response);
            assertEquals("user@example.com", response.getEmail());
            verify(userRepository).save(any(User.class));
            verify(kafkaTemplate).send(eq("USER-CREATED"), any(UserCreatedEvent.class));
        }
    }

    @Test
    public void testLogout() {
        // Given
        String token = "jwt-token";
        when(jwtService.extractTokenFromRequest(httpServletRequest)).thenReturn(token);
        when(jwtService.extractUsername(token)).thenReturn("user@example.com");

        // When
        String response = authenticationService.logout(httpServletRequest);

        // Then
        assertEquals("Logged out successfully", response);
        verify(tokenBlacklistService).addToBlacklist("user@example.com");
    }
}