package com.electric_titans.userservice.entity;

import com.electric_titans.userservice.enums.RoleEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class RoleTest {

    @Mock
    User user;

    @Test
    void testAllArgsConstructor() {
        Role role = new Role(0L, RoleEnum.SUPER_ADMIN, "description", List.of(user), Instant.now(), Instant.now());
        assertEquals(role);
    }

    @Test
    void testNoArgsConstructor() {
        Role role = new Role();
        Assertions.assertInstanceOf(Role.class, role);
    }

    @Test
    void testSetters() {
        Role role = new Role();
        role.setId(0L);
        role.setName(RoleEnum.SUPER_ADMIN);
        role.setDescription("description");
        role.setUsers(List.of(user));
        assertEquals(role);
    }

    void assertEquals(Role role) {
        Assertions.assertEquals(0L, role.getId());
        Assertions.assertEquals(RoleEnum.SUPER_ADMIN, role.getName());
        Assertions.assertEquals("description", role.getDescription());

        List<User> users = role.getUsers();
        Assertions.assertEquals(1, users.size());
        Assertions.assertTrue(users.contains(user));
    }
}
