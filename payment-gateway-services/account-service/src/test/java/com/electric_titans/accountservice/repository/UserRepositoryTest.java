package com.electric_titans.accountservice.repository;

import com.electric_titans.accountservice.entity.Role;
import com.electric_titans.accountservice.entity.User;
import com.electric_titans.accountservice.enums.RoleEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));


    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    public void testFindByEmail() {
        // Preparation
        String email = "sample@email.com";
        User user = new User();
        user.setEmail(email);
        Role role = new Role();
        role.setName(RoleEnum.SUPER_ADMIN);
        roleRepository.save(role);
        user.setRole(role);
        userRepository.save(user);

        // Testing
        User foundUser = userRepository.findByEmail(email).get();

        // Assertion
        Assertions.assertEquals("sample@email.com", foundUser.getEmail());
        Assertions.assertEquals(RoleEnum.SUPER_ADMIN, foundUser.getRole().getName());
    }
}
