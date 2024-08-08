package com.electric_titans.accountservice.service;

import com.electric_titans.accountservice.dto.request.AccountRequest;
import com.electric_titans.accountservice.dto.request.AccountStatusUpdateRequest;
import com.electric_titans.accountservice.dto.response.AccountResponse;
import com.electric_titans.accountservice.dto.response.AccountStatusUpdateResponse;
import com.electric_titans.accountservice.dto.response.BalanceResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface AccountService {

    AccountResponse createAccount(AccountRequest accountRequest, HttpServletRequest request);

    AccountResponse getAccountByAccountId(Long accountId);

    List<AccountResponse> getAllAccountsByUserId(Long userId);

    AccountResponse updateAccountByAccountId(Long accountId, AccountRequest accountRequest);

    void deactivateAccountByAccountId(Long accountId);

    BalanceResponse getCurrentBalanceByAccountId(Long accountId);

    AccountStatusUpdateResponse updateAccountStatusByAccountId(Long accountId, AccountStatusUpdateRequest request);
}
