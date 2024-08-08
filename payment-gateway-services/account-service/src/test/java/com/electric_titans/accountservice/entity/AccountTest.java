package com.electric_titans.accountservice.entity;

import com.electric_titans.accountservice.enums.AccountStatusEnum;
import com.electric_titans.accountservice.enums.AccountTypeEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;

@ExtendWith(MockitoExtension.class)
public class AccountTest {

    @Test
    void testAllArgsConstructor() {
        Account account = new Account(0L, 0L, AccountTypeEnum.SAVINGS, 0L, "usd", AccountStatusEnum.ACTIVE,
                                        Instant.ofEpochSecond(1), Instant.ofEpochSecond(1));
        assertEquals(account);
    }

    @Test
    void testNoArgsConstructor() {
        Account account = new Account();
        Assertions.assertInstanceOf(Account.class, account);
    }

    @Test
    void testSetters() {
        Account account = new Account();
        account.setId(0L);
        account.setUserId(0L);
        account.setAccountType(AccountTypeEnum.SAVINGS);
        account.setBalance(0L);
        account.setCurrency("usd");
        account.setStatus(AccountStatusEnum.ACTIVE);
        account.setCreatedAt(Instant.ofEpochSecond(1));
        account.setUpdatedAt(Instant.ofEpochSecond(1));
    }

    void assertEquals(Account account) {
        Assertions.assertEquals(0L, account.getId());
        Assertions.assertEquals(0L, account.getUserId());
        Assertions.assertEquals(AccountTypeEnum.SAVINGS, account.getAccountType());
        Assertions.assertEquals(0L, account.getBalance());
        Assertions.assertEquals("usd", account.getCurrency());
        Assertions.assertEquals(AccountStatusEnum.ACTIVE, account.getStatus());
        Assertions.assertEquals(Instant.ofEpochSecond(1), account.getCreatedAt());
        Assertions.assertEquals(Instant.ofEpochSecond(1), account.getUpdatedAt());
    }
}
