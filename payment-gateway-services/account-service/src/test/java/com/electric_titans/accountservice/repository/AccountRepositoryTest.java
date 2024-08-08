package com.electric_titans.accountservice.repository;

import com.electric_titans.accountservice.entity.Account;
import com.electric_titans.accountservice.enums.AccountStatusEnum;
import com.electric_titans.accountservice.enums.AccountTypeEnum;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;


@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccountRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));


    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
    }

    @Test
    public void testFindByUserId() {
        // Preparation
        Long userId = 1L;
        Account account1 = new Account();
        account1.setUserId(userId);
        account1.setAccountType(AccountTypeEnum.SAVINGS);
        account1.setBalance(0L);
        account1.setCurrency("usd");
        account1.setStatus(AccountStatusEnum.ACTIVE);
        account1.setCreatedAt(Instant.ofEpochSecond(1));
        account1.setUpdatedAt(Instant.ofEpochSecond(1));
        accountRepository.save(account1);

        Account account2 = new Account();
        account2.setUserId(userId);
        account2.setAccountType(AccountTypeEnum.SAVINGS);
        account2.setBalance(0L);
        account2.setCurrency("eur");
        account2.setStatus(AccountStatusEnum.ACTIVE);
        account2.setCreatedAt(Instant.ofEpochSecond(1));
        account2.setUpdatedAt(Instant.ofEpochSecond(1));
        accountRepository.save(account2);

        // Testing
        List<Account> accounts = accountRepository.findByUserId(userId);

        // Assertion
        Assertions.assertEquals(2, accounts.size());
    }
}
