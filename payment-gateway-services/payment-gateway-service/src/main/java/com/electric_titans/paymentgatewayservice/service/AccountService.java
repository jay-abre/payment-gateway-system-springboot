package com.electric_titans.paymentgatewayservice.service;

import com.stripe.model.Account;

import java.util.List;

public interface AccountService {
    void createAccount(String email, String country);
    void updateAccount(String email, String country);
    void deleteAccount(String email);
    Account getAccount(String accountId);
    List<Account> listAllAccounts();
}
