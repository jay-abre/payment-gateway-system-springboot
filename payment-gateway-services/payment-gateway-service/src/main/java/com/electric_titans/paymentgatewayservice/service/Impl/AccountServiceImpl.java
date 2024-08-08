package com.electric_titans.paymentgatewayservice.service.Impl;

import com.electric_titans.paymentgatewayservice.service.AccountService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import com.stripe.param.AccountCreateParams;
import com.stripe.param.AccountListParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {
    @Value("${stripe.api.key}")
    private String stripeApiKey;

    public AccountServiceImpl() {
        Stripe.apiKey = stripeApiKey;
    }

    @Override
    public void createAccount(String email, String country) {
        Stripe.apiKey = stripeApiKey;
        AccountCreateParams params = AccountCreateParams.builder()
                .setEmail(email)
                .setCountry(country)
                .setController(
                        AccountCreateParams.Controller.builder()
                                .setFees(
                                        AccountCreateParams.Controller.Fees.builder()
                                                .setPayer(AccountCreateParams.Controller.Fees.Payer.APPLICATION)
                                                .build()
                                )
                                .setLosses(
                                        AccountCreateParams.Controller.Losses.builder()
                                                .setPayments(AccountCreateParams.Controller.Losses.Payments.APPLICATION)
                                                .build()
                                )
                                .setStripeDashboard(
                                        AccountCreateParams.Controller.StripeDashboard.builder()
                                                .setType(AccountCreateParams.Controller.StripeDashboard.Type.EXPRESS)
                                                .build()
                                )
                                .build()
                )

                .setCapabilities(
                        AccountCreateParams.Capabilities.builder()
                                .setCardPayments(AccountCreateParams.Capabilities.CardPayments.builder().setRequested(true).build())
                                .setTransfers(AccountCreateParams.Capabilities.Transfers.builder().setRequested(true).build())
                                .build()

                )
                .build();

        try {
            Account account = Account.create(params);
        } catch (StripeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateAccount(String email, String country) {
        // Implement account update logic
    }

    @Override
    public void deleteAccount(String email) {
        // Implement account deletion logic
    }

    @Override
    public Account getAccount(String accountId) {
        Stripe.apiKey = stripeApiKey;

        try {
            Account account = Account.retrieve(accountId);
        } catch (StripeException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public List<Account> listAllAccounts() {
        Stripe.apiKey = stripeApiKey;
        List<Account> accountsList = new ArrayList<>();
        try {
            AccountListParams params = AccountListParams.builder()
                    .setLimit(10L)
                    .build();
            Iterable<Account> accounts = Account.list(params).autoPagingIterable();
            accounts.forEach(accountsList::add);
        } catch (StripeException e) {
            e.printStackTrace();
        }
        return accountsList;
    }
}