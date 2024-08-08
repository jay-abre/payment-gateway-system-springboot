package com.electric_titans.accountservice.controller;

import com.electric_titans.accountservice.dto.response.AccountResponse;
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

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private AccountServiceImpl accountService;

    @Test
    public void testGetAllAccountsByUserId() {
        // Given
        Long userId = 123L;
        AccountResponse accountResponse1 = new AccountResponse();
        accountResponse1.setId(1L);
        accountResponse1.setStatus(AccountStatusEnum.ACTIVE);
        accountResponse1.setAccountType(AccountTypeEnum.SAVINGS);

        AccountResponse accountResponse2 = new AccountResponse();
        accountResponse2.setId(2L);
        accountResponse2.setStatus(AccountStatusEnum.INACTIVE);
        accountResponse2.setAccountType(AccountTypeEnum.CHECKING);

        List<AccountResponse> accountResponses = List.of(accountResponse1, accountResponse2);
        Mockito.when(accountService.getAllAccountsByUserId(userId)).thenReturn(accountResponses);

        // When
        ResponseEntity<List<AccountResponse>> responseEntity = userController.getAllAccountsByUserId(userId);

        // Then
        Mockito.verify(accountService, Mockito.times(1)).getAllAccountsByUserId(userId);
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(accountResponses, responseEntity.getBody());
    }
}
