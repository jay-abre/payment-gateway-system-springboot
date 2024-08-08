package com.electric_titans.paymentgatewayservice.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;

public interface CustomerService {
    CustomerCollection listAllCustomers() throws StripeException;
    Customer getCustomer(String customerId) throws StripeException;
    void deleteCustomer(String customerId) throws StripeException;
}