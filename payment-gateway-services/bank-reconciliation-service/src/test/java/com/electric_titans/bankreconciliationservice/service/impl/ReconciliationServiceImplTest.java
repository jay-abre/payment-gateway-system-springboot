package com.electric_titans.bankreconciliationservice.service.impl;

import com.electric_titans.bankreconciliationservice.entity.Reconciliation;
import com.electric_titans.bankreconciliationservice.repository.ReconciliationRepository;

import com.electric_titans.bankreconciliationservice.service.Impl.ReconciliationServiceImpl;
import com.electric_titans.common.event.PaymentSuccessEvent;
import com.stripe.model.PaymentIntent;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReconciliationServiceImplTest {
    @Mock
    private ReconciliationRepository reconciliationRepository;

    @Mock
    private ReconciliationServiceImpl reconciliationService;

    @Test
    void testStartDepositReconciliation(){
        PaymentSuccessEvent paymentSuccessEvent = PaymentSuccessEvent.builder()
                        .paymentIntentId("str-1")
                        .transactionId(1L)
                        .fromAccountId(1L)
                        .transactionAmount(100L)
                        .customerId("cus-1")
                        .currency("usd")
                .build();

        Reconciliation reconciliation = Reconciliation.builder()
                        .reconciliationId(1L)
                .build();

        reconciliationService.startDepositReconciliation(paymentSuccessEvent);

        verify(reconciliationService, times(1)).startDepositReconciliation(any(PaymentSuccessEvent.class));
    }

    @Test
    void testStartWithdrawReconciliation(){
        PaymentSuccessEvent paymentSuccessEvent = PaymentSuccessEvent.builder()
                .paymentIntentId("str-1")
                .transactionId(1L)
                .fromAccountId(1L)
                .transactionAmount(100L)
                .customerId("cus-1")
                .currency("usd")
                .build();

        Reconciliation reconciliation = Reconciliation.builder()
                .reconciliationId(1L)
                .build();

        reconciliationService.startWithdrawReconciliation(paymentSuccessEvent);

        verify(reconciliationService, times(1)).startWithdrawReconciliation(any(PaymentSuccessEvent.class));
    }

    @Test
    void testStartTransferReconciliation(){
        PaymentSuccessEvent paymentSuccessEvent = PaymentSuccessEvent.builder()
                        .paymentIntentId("str-1")
                        .transactionId(1L)
                        .fromAccountId(1L)
                        .transactionAmount(100L)
                        .customerId("cus-1")
                        .currency("usd")
                .build();

        Reconciliation reconciliation = Reconciliation.builder()
                        .reconciliationId(1L)
                        .customerId("cus-1")
                        .stripeId("str-1")
                .build();

        reconciliationService.startTransferReconciliation(paymentSuccessEvent);

        verify(reconciliationService, times(1)).startTransferReconciliation(any(PaymentSuccessEvent.class));
        //verify(any(ReconciliationRepository.class), times(1)).save(any(Reconciliation.class));
    }

    @Test
    void testHandlePaymentIntentSucceeded(){


    }
}
