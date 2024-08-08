package com.electric_titans.userservice.entity;

import com.electric_titans.userservice.enums.RoleEnum;
import com.electric_titans.userservice.enums.UserStatusEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;

import java.time.Instant;
import java.util.Collection;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserTest {

    @InjectMocks
    User user;

    @Mock
    Role role;

    @Mock
    UserProfile userProfile;

    @Test
    void testAllArgsConstructor() {
        User user = new User(0L, "username", "email", "first name", "middle name",
                "last name", "mobile number", "password", false, role, UserStatusEnum.ACTIVE,
                "customer id", userProfile, Instant.now(), Instant.now());
        assertEquals(user);
    }

    @Test
    void testNoArgsConstructor() {
        User user = new User();
        Assertions.assertInstanceOf(User.class, user);
    }

    @Test
    void testSetters() {
        User user = new User();
        user.setId(0L);
        user.setUsername("username");
        user.setEmail("email");
        user.setFirstName("first name");
        user.setMiddleName("middle name");
        user.setLastName("last name");
        user.setMobileNumber("mobile number");
        user.setPassword("password");
        user.setRole(role);
        user.setStatus(UserStatusEnum.ACTIVE);
        assertEquals(user);
    }

    void assertEquals(User user) {
        Assertions.assertEquals(0L, user.getId());
        Assertions.assertEquals("username", user.getUsername());
        Assertions.assertEquals("email", user.getEmail());
        Assertions.assertEquals("password", user.getPassword());
        Assertions.assertEquals(role, user.getRole());
    }

    @Test
    void testGetAuthorities() {
        User user = new User();
        Role role = Mockito.mock(Role.class);

        Mockito.when(role.getName()).thenReturn(RoleEnum.ADMIN);
        user.setRole(role);

        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        Assertions.assertEquals(1, authorities.size());

        Optional<? extends GrantedAuthority> authority = authorities.stream().findFirst();
        Assertions.assertTrue(authorities.stream().findFirst().isPresent());
        Assertions.assertEquals("ROLE_ADMIN", authority.get().getAuthority());
    }

    @Test
    void testGetUsername() {
        Assertions.assertNull(user.getUsername());
    }

    @Test
    void testBooleanGetters() {
        Assertions.assertTrue(user.isAccountNonExpired());
        Assertions.assertTrue(user.isAccountNonLocked());
        Assertions.assertTrue(user.isCredentialsNonExpired());
        Assertions.assertTrue(user.isEnabled());
    }

}