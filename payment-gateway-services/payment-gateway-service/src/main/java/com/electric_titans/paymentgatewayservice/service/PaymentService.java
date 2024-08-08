package com.electric_titans.paymentgatewayservice.service;

import com.electric_titans.common.event.TransactionCreatedEvent;
import com.electric_titans.common.event.TransferCreatedEvent;
import com.electric_titans.paymentgatewayservice.entity.Payment;
import com.stripe.exception.StripeException;
import com.stripe.model.Balance;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Payout;
import com.stripe.model.Transfer;

public interface PaymentService {
     void createDeposit(TransactionCreatedEvent payment) throws StripeException;
     void createWithdraw(TransactionCreatedEvent payment) throws StripeException;
     void createTransfer(TransactionCreatedEvent transfer) throws StripeException;
     PaymentIntent confirmPayment(Payment payment) throws StripeException;
     //Transfer createTransfer(TransferCreatedEvent transfer) throws StripeException;
     //Balance getBalance() throws StripeException;
}