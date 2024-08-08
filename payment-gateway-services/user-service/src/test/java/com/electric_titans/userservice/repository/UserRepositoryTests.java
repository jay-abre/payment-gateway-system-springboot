package com.electric_titans.userservice.repository;

import com.electric_titans.userservice.entity.User;
import com.electric_titans.userservice.enums.KycStatusEnum;
import com.electric_titans.userservice.enums.UserStatusEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserRepositoryTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    public void testFindByEmail() {
        // Given
        User user = new User();
        user.setEmail("sample@email.com");
        Mockito.when(userRepository.findByEmail(ArgumentMatchers.anyString())).thenReturn(Optional.of(user));

        // When
        Optional<User> foundUser = userRepository.findByEmail(user.getEmail());

        // Then
        assertTrue(foundUser.isPresent());
    }

    @Test
    public void testExistsByEmail() {
        // Given
        boolean isExists = true;
        String existingEmail = "sample@email.com";
        Mockito.when(userRepository.existsByEmail(existingEmail)).thenReturn(isExists);

        // When
        boolean existsByEmail = userRepository.existsByEmail(existingEmail);

        // Then
        assertTrue(existsByEmail);
    }

    @Test
    public void testFindByStatus() {
        // Given
        Pageable pageable = PageRequest.of(1, 10);
        Page<User> userPage = Page.empty();
        Mockito.when(userRepository.findByStatus(UserStatusEnum.ACTIVE, pageable)).thenReturn(userPage);

        // When
        Page<User> findByStatus = userRepository.findByStatus(UserStatusEnum.ACTIVE, pageable);

        // Then
        assertTrue(userPage.isEmpty(), String.valueOf(findByStatus.isEmpty()));
    }

    @Test
    public void testFindByUserProfile_kycStatus() {
        // Given
        Pageable pageable = PageRequest.of(1, 10);
        Page<User> userPage = Page.empty();
        Mockito.when(userRepository.findByUserProfile_kycStatus(KycStatusEnum.PENDING, pageable)).thenReturn(userPage);

        // When
        Page<User> findByStatus = userRepository.findByUserProfile_kycStatus(KycStatusEnum.PENDING, pageable);

        // Then
        assertTrue(userPage.isEmpty(), String.valueOf(findByStatus.isEmpty()));
    }
}