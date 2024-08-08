package com.electric_titans.accountservice.service;

import com.electric_titans.accountservice.dto.request.AccountRequest;
import com.electric_titans.accountservice.dto.request.AccountStatusUpdateRequest;
import com.electric_titans.accountservice.dto.response.AccountResponse;
import com.electric_titans.accountservice.dto.response.AccountStatusUpdateResponse;
import com.electric_titans.accountservice.dto.response.BalanceResponse;
import com.electric_titans.accountservice.entity.Account;
import com.electric_titans.accountservice.enums.AccountStatusEnum;
import com.electric_titans.accountservice.enums.AccountTypeEnum;
import com.electric_titans.accountservice.exception.ResourceNotFoundException;
import com.electric_titans.accountservice.repository.AccountRepository;
import com.electric_titans.accountservice.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @InjectMocks
    AccountServiceImpl accountService;

    @Mock
    AccountRepository accountRepository;

//    @Test
//    public void testCreateAccount() {
//        // Given
//        AccountRequest accountRequest = new AccountRequest();
//        accountRequest.setStatus(AccountStatusEnum.ACTIVE);
//        accountRequest.setAccountType(AccountTypeEnum.SAVINGS);
//
//        Account account = new Account();
//        account.setId(1L);
//        account.setStatus(AccountStatusEnum.ACTIVE);
//        account.setAccountType(AccountTypeEnum.SAVINGS);
//        Mockito.when(accountRepository.save(Mockito.any())).thenReturn(account);
//
//        // When
//        AccountResponse result = accountService.createAccount(accountRequest);
//
//        // Then
//        Mockito.verify(accountRepository, times(1)).save(Mockito.any());
//        Assertions.assertEquals(account.getId(), result.getId());
//        Assertions.assertEquals(account.getStatus(), result.getStatus());
//        Assertions.assertEquals(account.getAccountType(), result.getAccountType());
//    }

    @Test
    public void testGetAccountByAccountId() {
        // Given
        Long accountId = 123L;
        Account account = new Account();
        account.setId(accountId);
        Mockito.when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // When
        AccountResponse result = accountService.getAccountByAccountId(accountId);

        // Then
        Mockito.verify(accountRepository, times(1)).findById(accountId);
        Assertions.assertEquals(account.getId(), result.getId());
    }

    @Test
    public void testGetAllAccountsByUserId() {
        // Given
        Long userId = 123L;

        Account account1 = new Account();
        account1.setId(1L);
        account1.setUserId(userId);

        Account account2 = new Account();
        account2.setId(2L);
        account2.setUserId(userId);

        List<Account> accounts = List.of(account1, account2);
        Mockito.when(accountRepository.findByUserId(userId)).thenReturn(accounts);

        // When
        List<AccountResponse> result = accountService.getAllAccountsByUserId(userId);

        // Then
        Mockito.verify(accountRepository, times(1)).findByUserId(userId);
        Assertions.assertEquals(accounts.size(), result.size());
        Assertions.assertEquals(account1.getUserId(), result.get(0).getUserId());
        Assertions.assertEquals(account2.getUserId(), result.get(1).getUserId());
    }

    @Test
    public void testUpdateAccountByAccountId() {
        // Given
        Long accountId = 123L;
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setUserId(456L);
        accountRequest.setAccountType(AccountTypeEnum.SAVINGS);
        accountRequest.setBalance(1000L);
        accountRequest.setCurrency("USD");
        accountRequest.setStatus(AccountStatusEnum.ACTIVE);

        Account account = new Account();
        account.setId(accountId);
        Mockito.when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        Mockito.when(accountRepository.save(Mockito.any())).thenReturn(account);

        // When
        AccountResponse result = accountService.updateAccountByAccountId(accountId, accountRequest);

        // Then
        Mockito.verify(accountRepository, times(1)).findById(accountId);
        Mockito.verify(accountRepository, times(1)).save(Mockito.any());
        Assertions.assertEquals(accountRequest.getUserId(), result.getUserId());
        Assertions.assertEquals(accountRequest.getAccountType(), result.getAccountType());
        Assertions.assertEquals(accountRequest.getBalance(), result.getBalance());
        Assertions.assertEquals(accountRequest.getCurrency(), result.getCurrency());
        Assertions.assertEquals(accountRequest.getStatus(), result.getStatus());
    }

    @Test
    public void testUpdateAccountByAccountIdNotFound() {
        // Given
        Long accountId = 789L;
        Mockito.when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // When and Then
        Assertions.assertThrows(ResourceNotFoundException.class, () -> accountService.updateAccountByAccountId(accountId, new AccountRequest()));
    }

    @Test
    public void testDeactivateAccountByAccountId() {
        // Given
        Long accountId = 123L;
        Account account = new Account();
        account.setId(accountId);
        Mockito.when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // When
        accountService.deactivateAccountByAccountId(accountId);

        // Then
        Mockito.verify(accountRepository, times(1)).findById(accountId);
        Mockito.verify(accountRepository, times(1)).save(account);
        Assertions.assertEquals(AccountStatusEnum.INACTIVE, account.getStatus());
    }

    @Test
    public void testDeactivateAccountByAccountIdNotFound() {
        // Given
        Long accountId = 456L;
        Mockito.when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // When and Then
        Assertions.assertThrows(ResourceNotFoundException.class, () -> accountService.deactivateAccountByAccountId(accountId));
    }

    @Test
    public void testGetCurrentBalanceByAccountId() {
        // Given
        Long accountId = 123L;
        Long balance = 1000L;
        Account account = new Account();
        account.setId(accountId);
        account.setBalance(balance);
        Mockito.when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // When
        BalanceResponse result = accountService.getCurrentBalanceByAccountId(accountId);

        // Then
        Mockito.verify(accountRepository, times(1)).findById(accountId);
        Assertions.assertEquals(balance, result.getBalance());
    }

    @Test
    public void testGetCurrentBalanceByAccountIdNotFound() {
        // Given
        Long accountId = 456L;
        Mockito.when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // When and Then
        Assertions.assertThrows(ResourceNotFoundException.class, () -> accountService.getCurrentBalanceByAccountId(accountId));
    }

    @Test
    public void testUpdateAccountStatusByAccountId() {
        // Given
        Long accountId = 123L;
        AccountStatusUpdateRequest request = new AccountStatusUpdateRequest();
        request.setStatus(AccountStatusEnum.ACTIVE);

        Account account = new Account();
        account.setId(accountId);
        Mockito.when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        Mockito.when(accountRepository.save(Mockito.any())).thenReturn(account);

        // When
        AccountStatusUpdateResponse result = accountService.updateAccountStatusByAccountId(accountId, request);

        // Then
        Mockito.verify(accountRepository, times(1)).findById(accountId);
        Mockito.verify(accountRepository, times(1)).save(Mockito.any());
        Assertions.assertEquals(request.getStatus(), result.getStatus());
        Assertions.assertEquals("Account status updated successfully.", result.getMessage());
    }

    @Test
    public void testUpdateAccountStatusByAccountIdNotFound() {
        // Given
        Long accountId = 456L;
        Mockito.when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        // When and Then
        Assertions.assertThrows(ResourceNotFoundException.class, () -> accountService.updateAccountStatusByAccountId(accountId, new AccountStatusUpdateRequest()));
    }
}
