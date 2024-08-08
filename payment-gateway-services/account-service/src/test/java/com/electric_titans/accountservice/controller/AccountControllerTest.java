package com.electric_titans.accountservice.controller;

import com.electric_titans.accountservice.dto.request.AccountRequest;
import com.electric_titans.accountservice.dto.request.AccountStatusUpdateRequest;
import com.electric_titans.accountservice.dto.response.AccountResponse;
import com.electric_titans.accountservice.dto.response.AccountStatusUpdateResponse;
import com.electric_titans.accountservice.dto.response.BalanceResponse;
import com.electric_titans.accountservice.entity.Account;
import com.electric_titans.accountservice.enums.AccountStatusEnum;
import com.electric_titans.accountservice.enums.AccountTypeEnum;
import com.electric_titans.accountservice.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

    @InjectMocks
    private AccountController accountController;

    @Mock
    private AccountServiceImpl accountService;

//    @Test
//    public void testCreateAccount() {
//        // Given
//        AccountRequest accountRequest = new AccountRequest();
//        accountRequest.setUserId(123L);
//        accountRequest.setAccountType(AccountTypeEnum.SAVINGS);
//
//        AccountResponse accountResponse = new AccountResponse();
//        accountResponse.setId(456L);
//
//        Mockito.when(accountService.createAccount(accountRequest)).thenReturn(accountResponse);
//
//        // When
//        ResponseEntity<AccountResponse> responseEntity = accountController.createAccount(accountRequest);
//
//        // Then
//        Mockito.verify(accountService, Mockito.times(1)).createAccount(accountRequest);
//        Assertions.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
//        Assertions.assertEquals(accountResponse, responseEntity.getBody());
//    }

    @Test
    public void testGetAccountByAccountId() {
        // Given
        Long accountId = 123L;
        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setId(accountId);

        Mockito.when(accountService.getAccountByAccountId(accountId)).thenReturn(accountResponse);

        // When
        ResponseEntity<AccountResponse> responseEntity = accountController.getAccountByAccountId(accountId);

        // Then
        Mockito.verify(accountService, Mockito.times(1)).getAccountByAccountId(accountId);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(accountResponse, responseEntity.getBody());
    }

    @Test
    public void testUpdateAccountByAccountId() {
        // Given
        Long accountId = 123L;
        AccountRequest accountRequest = new AccountRequest();
        accountRequest.setUserId(456L);
        accountRequest.setAccountType(AccountTypeEnum.SAVINGS);

        AccountResponse updatedAccountResponse = new AccountResponse();
        updatedAccountResponse.setId(accountId);
        updatedAccountResponse.setUserId(accountRequest.getUserId());
        updatedAccountResponse.setAccountType(accountRequest.getAccountType());

        Account accountToUpdate = new Account();
        accountToUpdate.setId(accountId);

        Mockito.when(accountService.updateAccountByAccountId(accountId, accountRequest)).thenReturn(updatedAccountResponse);

        // When
        ResponseEntity<AccountResponse> responseEntity = accountController.updateAccountByAccountId(accountId, accountRequest);

        // Then
        Mockito.verify(accountService, Mockito.times(1)).updateAccountByAccountId(accountId, accountRequest);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(updatedAccountResponse, responseEntity.getBody());
    }

    @Test
    public void testDeactivateAccountByAccountId() {
        // Given
        Long accountId = 123L;
        Mockito.doNothing().when(accountService).deactivateAccountByAccountId(accountId);

        // When
        ResponseEntity<Void> responseEntity = accountController.deactivateAccountByAccountId(accountId);

        // Then
        Mockito.verify(accountService, Mockito.times(1)).deactivateAccountByAccountId(accountId);
        Assertions.assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
    }

    @Test
    public void testGetCurrentBalanceByAccountId() {
        // Given
        Long accountId = 123L;
        Long balance = 1000L;
        BalanceResponse balanceResponse = new BalanceResponse();
        balanceResponse.setBalance(balance);

        Mockito.when(accountService.getCurrentBalanceByAccountId(accountId)).thenReturn(balanceResponse);

        // When
        ResponseEntity<BalanceResponse> responseEntity = accountController.getCurrentBalanceByAccountId(accountId);

        // Then
        Mockito.verify(accountService, Mockito.times(1)).getCurrentBalanceByAccountId(accountId);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(balance, responseEntity.getBody().getBalance());
    }

    @Test
    public void testUpdateAccountStatusByAccountId() {
        // Given
        Long accountId = 123L;
        AccountStatusEnum updatedStatus = AccountStatusEnum.INACTIVE;
        AccountStatusUpdateRequest request = new AccountStatusUpdateRequest();
        request.setStatus(updatedStatus);

        AccountStatusUpdateResponse expectedResponse = new AccountStatusUpdateResponse();
        expectedResponse.setMessage("Account status updated successfully.");
        expectedResponse.setStatus(updatedStatus);
        Mockito.when(accountService.updateAccountStatusByAccountId(accountId, request)).thenReturn(expectedResponse);

        // When
        ResponseEntity<AccountStatusUpdateResponse> responseEntity = accountController.updateAccountStatusByAccountId(accountId, request);

        // Then
        Mockito.verify(accountService, Mockito.times(1)).updateAccountStatusByAccountId(accountId, request);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(expectedResponse, responseEntity.getBody());
    }
}
