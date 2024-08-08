package com.electric_titans.accountservice.entity;

import com.electric_titans.accountservice.enums.RoleEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RoleTest {

    @Test
    void testAllArgsConstructor() {
        Role role = new Role(0L, RoleEnum.SUPER_ADMIN);
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
        assertEquals(role);
    }

    void assertEquals(Role role) {
        Assertions.assertEquals(0L, role.getId());
        Assertions.assertEquals(RoleEnum.SUPER_ADMIN, role.getName());
    }
}
