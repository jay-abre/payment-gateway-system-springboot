package com.electric_titans.notificationservice.service;

import com.electric_titans.common.event.PaymentSuccessEvent;
import com.electric_titans.common.event.ReconciliationEvent;
import com.electric_titans.common.event.TransactionCreatedEvent;
import com.electric_titans.common.event.UserCreatedEvent;

import java.io.IOException;

public interface MailService {

    String userCreatedEmail(UserCreatedEvent userCreatedEvent) throws IOException;

    String transactionCreatedEmail(TransactionCreatedEvent transactionCreatedEvent) throws IOException;

    String paymentSucceededEmail(PaymentSuccessEvent depositSuccessEvent) throws IOException;

    String reconciliationFinishedEmail(ReconciliationEvent reconciliationEvent) throws IOException;
}
