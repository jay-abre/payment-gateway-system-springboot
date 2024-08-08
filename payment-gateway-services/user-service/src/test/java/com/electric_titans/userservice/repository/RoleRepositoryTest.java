package com.electric_titans.userservice.repository;

import com.electric_titans.userservice.entity.Role;
import com.electric_titans.userservice.enums.RoleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class RoleRepositoryTest {

    @Mock
    private RoleRepository roleRepository;

    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setName(RoleEnum.USER);
        role.setDescription("A standard user role");
    }

    @Test
    public void testFindByName() {
        // Given
        Mockito.when(roleRepository.findByName(RoleEnum.USER)).thenReturn(Optional.of(role));

        // When
        Optional<Role> foundRole = roleRepository.findByName(RoleEnum.USER);

        // Then
        assertTrue(foundRole.isPresent(), "Role should be found by name");
        assertEquals(role.getName(), foundRole.get().getName(), "Found role should have the same name as the mocked role");
    }
}