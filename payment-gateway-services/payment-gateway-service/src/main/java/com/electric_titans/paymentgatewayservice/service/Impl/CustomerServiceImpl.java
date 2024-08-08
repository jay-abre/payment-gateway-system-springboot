package com.electric_titans.paymentgatewayservice.service.Impl;

import com.electric_titans.paymentgatewayservice.service.CustomerService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.stripe.param.CustomerListParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    public CustomerServiceImpl() {
        Stripe.apiKey = stripeApiKey;
    }

    @Override
    public CustomerCollection listAllCustomers() throws StripeException {
        Stripe.apiKey = stripeApiKey;

        CustomerListParams params = CustomerListParams.builder()
                .setLimit(10L)
                .build();
        return Customer.list(params);
    }
    @Override
    public Customer getCustomer(String customerId) throws StripeException {
        Stripe.apiKey = stripeApiKey;
        return Customer.retrieve(customerId);
    }
    @Override
    public void deleteCustomer(String customerId) throws StripeException {
        Stripe.apiKey = stripeApiKey;
        Customer customer = Customer.retrieve(customerId);
        customer.delete();
    }
}