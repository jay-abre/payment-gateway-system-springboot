package com.electric_titans.userservice.service;

import com.electric_titans.userservice.dto.request.RegisterRequest;
import com.electric_titans.userservice.dto.request.UserProfileRequest;
import com.electric_titans.userservice.dto.request.VerifyKycRequest;
import com.electric_titans.userservice.dto.response.StatusResponse;
import com.electric_titans.userservice.dto.response.UserPageResponse;
import com.electric_titans.userservice.dto.response.UserResponse;
import com.electric_titans.userservice.entity.Role;
import com.electric_titans.userservice.entity.User;
import com.electric_titans.userservice.entity.UserProfile;
import com.electric_titans.userservice.enums.KycStatusEnum;
import com.electric_titans.userservice.enums.RoleEnum;
import com.electric_titans.userservice.enums.UserStatusEnum;
import com.electric_titans.userservice.exception.ImageProcessingException;
import com.electric_titans.userservice.exception.ResourceNotFoundException;
import com.electric_titans.userservice.exception.UserAlreadyExistsException;
import com.electric_titans.userservice.mapper.UserMapper;
import com.electric_titans.userservice.repository.UserProfileRepository;
import com.electric_titans.userservice.repository.UserRepository;
import com.electric_titans.userservice.service.impl.UserServiceImpl;
import com.stripe.exception.StripeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserResponse userResponse;
    private Page<User> userPage;
    private Role role;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setUsername("username");
        user.setFirstName("first name");
        user.setLastName("last name");
        user.setMobileNumber("mobile number");
        user.setPassword("password");
        user.setBlacklisted(true);
        user.setRole(mock(Role.class));
        user.setStatus(UserStatusEnum.ACTIVE);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        role = new Role();
        role.setName(RoleEnum.ADMIN);

        userResponse = new UserResponse();
        userResponse.setEmail("test@example.com");
        userPage = new PageImpl<>(List.of(user));

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password");
    }

    @Test
    public void testGetAuthenticatedUser() {
        // Given
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(user);

        // When
        UserResponse result = userService.getAuthenticatedUser();

        // Then
        assertEquals(userResponse.getEmail(), result.getEmail(), "The email should match the authenticated user's email");
    }

    @Test
    public void testGetAllUsers() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("email").ascending());
        when(userRepository.findAll(pageable)).thenReturn(userPage);

        // When
        UserPageResponse result = userService.getAllUsers(0, 10, "email", "asc", null, null);

        // Then
        assertEquals(1, result.getContent().size(), "The number of users should match");
        assertEquals(userResponse.getEmail(), result.getContent().get(0).getEmail(), "The email should match the mocked user's email");
    }

    @Test
    public void testCreateAdministrator_UserAlreadyExists() {
        // Mocking the repository
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // Testing and asserting exception
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.createAdministrator(registerRequest);
        });

        // Verify that no other interactions happened
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testGetUserById_NotFound() {
        // Mocking the repository
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Testing and asserting exception
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(1L);
        });

        // Verify interactions
        verify(userRepository).findById(1L);
        verify(userMapper, never()).userToUserResponse(any(User.class));
    }

    @Test
    public void testUpdateUser_UserNotFound() {
        // Mocking the repository
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Testing and asserting exception
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUser(1L, registerRequest);
        });

        // Verify interactions
        verify(userRepository).findById(1L);
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).userToUserResponse(any(User.class));
    }

    @Test
    public void testUpdateUser_CustomerIdNotFound() {
        // Mocking the repository
        user.setCustomerId(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Testing and asserting exception
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUser(1L, registerRequest);
        });

        // Verify interactions
        verify(userRepository).findById(1L);
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
        verify(userMapper, never()).userToUserResponse(any(User.class));
    }

    @Test
    public void testUpdateUser_EmailAlreadyExists() {
        // Mocking the repository
        lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        lenient().when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // Testing and asserting exception
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUser(1L, registerRequest);
        });
    }

    @Test
    public void testUpdateUser_StripeExceptionOnRetrieve() throws StripeException {
        // Mocking the repository and Stripe Customer
        lenient().when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        lenient().when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        // Testing and asserting exception
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateUser(1L, registerRequest);
        });
    }

    @Test
    public void testDeactivateUser_Success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setStatus(UserStatusEnum.ACTIVE);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deactivateUser(userId);

        assertEquals(UserStatusEnum.INACTIVE, user.getStatus());
        verify(userRepository, times(1)).save(user);
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testDeactivateUser_UserNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.deactivateUser(userId);
        });

        assertEquals("User not found with id : 1", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testCheckStatusByUserId_Success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setStatus(UserStatusEnum.ACTIVE);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        StatusResponse statusResponse = userService.checkStatusByUserId(userId);

        assertNotNull(statusResponse);
        assertEquals(UserStatusEnum.ACTIVE, statusResponse.getStatus());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testCheckStatusByUserId_UserNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.checkStatusByUserId(userId);
        });

        assertEquals("User not found with id : 1", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void testUpdateContactInformation_UserNotFound() {
        Long userId = 1L;
        UserProfileRequest userProfileRequest = new UserProfileRequest();
        MockMultipartFile idPicture = new MockMultipartFile("idPicture", "test.jpg", "image/jpeg", "test image content".getBytes());

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.updateContactInformation(userId, userProfileRequest, idPicture);
        });

        assertEquals("User not found with id : 1", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userProfileRepository, never()).save(any(UserProfile.class));
    }

    @Test
    public void testUpdateContactInformation_ImageProcessingException() throws IOException {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");
        user.setCustomerId("cus_test");

        UserProfileRequest userProfileRequest = new UserProfileRequest();
        MockMultipartFile idPicture = mock(MockMultipartFile.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(idPicture.getBytes()).thenThrow(new IOException("Failed to process image"));

        ImageProcessingException exception = assertThrows(ImageProcessingException.class, () -> {
            userService.updateContactInformation(userId, userProfileRequest, idPicture);
        });

        assertEquals("Failed to process image.", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userProfileRepository, never()).save(any(UserProfile.class));
    }

    @Test
    public void testVerifyUserKyc_Success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");

        UserProfile userProfile = new UserProfile();
        userProfile.setId(1L);
        user.setUserProfile(userProfile);

        VerifyKycRequest verifyKycRequest = new VerifyKycRequest();
        verifyKycRequest.setKycStatusEnum(KycStatusEnum.FULLY_VERIFIED);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(userProfile);

        UserResponse userResponse = userService.verifyUserKyc(userId, verifyKycRequest);

        assertNotNull(userResponse);
        assertEquals(KycStatusEnum.FULLY_VERIFIED, userResponse.getUserProfile().getKycStatus());
        verify(userRepository, times(1)).findById(userId);
        verify(userProfileRepository, times(1)).save(userProfile);
    }

    @Test
    public void testVerifyUserKyc_UserNotFound() {
        Long userId = 1L;
        VerifyKycRequest verifyKycRequest = new VerifyKycRequest();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            userService.verifyUserKyc(userId, verifyKycRequest);
        });

        assertEquals("User not found with id : 1", exception.getMessage());
        verify(userRepository, times(1)).findById(userId);
        verify(userProfileRepository, never()).save(any(UserProfile.class));
    }

    @Test
    void testGetKycStatus_FullyVerified() {
        Long userId = 1L;

        UserProfile userProfile = mock(UserProfile.class);
        when(userProfile.getKycStatus()).thenReturn(KycStatusEnum.FULLY_VERIFIED);

        User user = mock(User.class);
        when(user.getUserProfile()).thenReturn(userProfile);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        boolean result = userService.getKycStatus(userId);

        assertTrue(result, "KYC status should be fully verified.");
    }

    @Test
    void testGetKycStatus_NotFullyVerified() {
        Long userId = 1L;

        UserProfile userProfile = mock(UserProfile.class);
        when(userProfile.getKycStatus()).thenReturn(KycStatusEnum.PENDING);

        User user = mock(User.class);
        when(user.getUserProfile()).thenReturn(userProfile);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        boolean result = userService.getKycStatus(userId);

        assertFalse(result, "KYC status should not be fully verified.");
    }

    @Test
    void testGetKycStatus_UserNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException thrown = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getKycStatus(userId),
                "Expected getKycStatus to throw, but it didn't"
        );

        assertEquals("User not found with id : 1", thrown.getMessage());
    }
}