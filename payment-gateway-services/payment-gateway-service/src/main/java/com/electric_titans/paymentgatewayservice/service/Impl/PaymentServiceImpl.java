package com.electric_titans.paymentgatewayservice.service.Impl;

import com.electric_titans.common.event.PaymentSuccessEvent;
import com.electric_titans.common.event.TransactionCreatedEvent;
import com.electric_titans.paymentgatewayservice.entity.Payment;
import com.electric_titans.paymentgatewayservice.repository.PaymentRepository;
import com.electric_titans.paymentgatewayservice.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PaymentRepository paymentRepository;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Autowired
    public PaymentServiceImpl(KafkaTemplate<String, Object> kafkaTemplate, PaymentRepository paymentRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.paymentRepository = paymentRepository;
        Stripe.apiKey = stripeApiKey;
    }

    @Override
    @Transactional
    @KafkaListener(topics = "DEPOSIT-REQUEST", groupId = "payment-gateway-service")
    public void createDeposit(TransactionCreatedEvent paymentRequest) throws StripeException {
        Stripe.apiKey = stripeApiKey;
        logger.info("Creating deposit for customer: {}", paymentRequest.getCustomerId());

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(paymentRequest.getAmount())
                .setCurrency(paymentRequest.getCurrency())
                .setCustomer(paymentRequest.getCustomerId())
                .setPaymentMethod(paymentRequest.getPaymentMethod())
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .setAllowRedirects(
                                        PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER
                                )
                                .build()
                )

                .putMetadata("transaction-type", "deposit")
                .build();


        PaymentIntent paymentIntent = PaymentIntent.create(params);

        Payment paymentEntity = mapPaymentIntentToPaymentEntity(paymentIntent, paymentRequest);

        paymentEntity.setPaymentIntentId(paymentIntent.getId());

        Payment savedPayment = paymentRepository.save(paymentEntity);
        kafkaTemplate.send("DEPOSIT-CREATED", PaymentSuccessEvent.builder()
                .paymentId(savedPayment.getId().toString())
                .transactionId(savedPayment.getTransactionId())
                .paymentIntentId(savedPayment.getPaymentIntentId())
                .fromAccountId(savedPayment.getFromAccountId())
                .transactionAmount(savedPayment.getAmount())
                .customerId(savedPayment.getCustomerId())
                .currency(savedPayment.getCurrency())
                .build());
    }

    @Override
    @Transactional
    @KafkaListener(topics = "WITHDRAW-REQUEST", groupId = "payment-gateway-service")
    public void createWithdraw(TransactionCreatedEvent paymentRequest) throws StripeException {
        Stripe.apiKey = stripeApiKey;
        logger.info("Creating withdraw for customer: {}", paymentRequest.getCustomerId());

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(paymentRequest.getAmount())
                .setCurrency(paymentRequest.getCurrency())
                .setCustomer(paymentRequest.getCustomerId())
                .setPaymentMethod(paymentRequest.getPaymentMethod())
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .setAllowRedirects(
                                        PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER
                                )
                                .build()
                )

                .putMetadata("transaction-type", "withdraw")
                .build();


        PaymentIntent paymentIntent = PaymentIntent.create(params);

        Payment paymentEntity = mapPaymentIntentToPaymentEntity(paymentIntent, paymentRequest);

        paymentEntity.setPaymentIntentId(paymentIntent.getId());
        paymentEntity.setToAccountId(paymentRequest.getToAccountId());

        Payment savedPayment = paymentRepository.save(paymentEntity);
        kafkaTemplate.send("WITHDRAW-CREATED", PaymentSuccessEvent.builder()
                .paymentId(savedPayment.getId().toString())
                .transactionId(savedPayment.getTransactionId())
                .paymentIntentId(savedPayment.getPaymentIntentId())
                .fromAccountId(savedPayment.getFromAccountId())
                .toAccountId(savedPayment.getToAccountId())
                .transactionAmount(savedPayment.getAmount())
                .customerId(savedPayment.getCustomerId())
                .currency(savedPayment.getCurrency())
                .build());
    }

    @Override
    @Transactional
    @KafkaListener(topics = "TRANSFER-REQUEST", groupId = "payment-gateway-service")
    public void createTransfer(TransactionCreatedEvent paymentRequest) throws StripeException {
        Stripe.apiKey = stripeApiKey;
        logger.info("Creating transfer for customer: {}", paymentRequest.getCustomerId());

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(paymentRequest.getAmount())
                .setCurrency(paymentRequest.getCurrency())
                .setCustomer(paymentRequest.getCustomerId())
                .setPaymentMethod(paymentRequest.getPaymentMethod())
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .setAllowRedirects(
                                        PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER
                                )
                                .build()
                )

                .putMetadata("transaction-type", "transfer")
                .build();


        PaymentIntent paymentIntent = PaymentIntent.create(params);

        Payment paymentEntity = mapPaymentIntentToPaymentEntity(paymentIntent, paymentRequest);

        paymentEntity.setPaymentIntentId(paymentIntent.getId());

        Payment savedPayment = paymentRepository.save(paymentEntity);
        kafkaTemplate.send("TRANSFER-CREATED", PaymentSuccessEvent.builder()
                .paymentId(savedPayment.getId().toString())
                .transactionId(savedPayment.getTransactionId())
                .paymentIntentId(savedPayment.getPaymentIntentId())
                .fromAccountId(savedPayment.getFromAccountId())
                .transactionAmount(savedPayment.getAmount())
                .customerId(savedPayment.getCustomerId())
                .currency(savedPayment.getCurrency())
                .build());
    }


    @Override
    public PaymentIntent confirmPayment(Payment paymentRequest) throws StripeException {
        if (paymentRequest.getPaymentMethodId() == null || paymentRequest.getPaymentMethodId().isEmpty()) {
            logger.error("PaymentMethodId is null or empty for PaymentIntent confirmation.");
            throw new IllegalArgumentException("PaymentMethodId cannot be null or empty.");
        }

        logger.info("Starting payment confirmation for PaymentIntent ID: {}", paymentRequest.getPaymentMethodId());
        try {
            Stripe.apiKey = stripeApiKey;
            PaymentIntent resource = PaymentIntent.retrieve(paymentRequest.getPaymentIntentId()); // Corrected to use PaymentIntentId
            PaymentIntentConfirmParams params = PaymentIntentConfirmParams.builder()
                    .setPaymentMethod(paymentRequest.getPaymentMethodId())
                    .build();
            PaymentIntent confirmedPaymentIntent = resource.confirm(params);
            logger.info("Payment confirmed successfully for PaymentIntent ID: {}", paymentRequest.getPaymentIntentId());


            logger.info("Confirmed PaymentIntent status: {}", confirmedPaymentIntent.getStatus());

            Payment paymentEntity = paymentRepository.findByPaymentMethodId(paymentRequest.getPaymentMethodId())
                    .orElseThrow(() -> new IllegalArgumentException("Payment not found with PaymentMethodId: " + paymentRequest.getPaymentMethodId()));
            paymentEntity.setStatus(confirmedPaymentIntent.getStatus().toUpperCase());
            paymentEntity.setUpdatedAt(ZonedDateTime.now());
            Payment savedPayment = paymentRepository.save(paymentEntity);

            String transactionType = resource.getMetadata().get("transaction-type");
            logger.info("Transaction Type: {}", transactionType);

            switch (transactionType) {
                case "deposit":
                    handleKafka(savedPayment, "DEPOSIT-SUCCESS");
                    break;
                case "withdraw":
                    handleKafka(savedPayment, "WITHDRAW-SUCCESS");
                    break;
                case "transfer":
                    handleKafka(savedPayment, "TRANSFER-SUCCESS");
                    break;
            }

            return confirmedPaymentIntent;
        } catch (StripeException e) {
            logger.error("Failed to confirm payment for PaymentIntent ID: {}", paymentRequest.getPaymentIntentId(), e);
            throw e;
        }
    }

    private Payment mapPaymentIntentToPaymentEntity(PaymentIntent paymentIntent, TransactionCreatedEvent paymentRequest) {
        return Payment.builder()
                .transactionId(paymentRequest.getTransactionId())
                .fromAccountId(paymentRequest.getFromAccountId())
                .toAccountId(paymentRequest.getToAccountId())
                .paymentIntentId(paymentIntent.getId())
                .amount(paymentIntent.getAmount())
                .customerId(paymentIntent.getCustomer())
                .currency(paymentIntent.getCurrency())
                .status(paymentIntent.getStatus().toUpperCase())
                .paymentMethodId(paymentIntent.getPaymentMethod())
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();
    }

    private void handleKafka(Payment savedPayment, String topic) {
        kafkaTemplate.send(topic, PaymentSuccessEvent.builder()
                .paymentId(savedPayment.getId().toString())
                .transactionId(savedPayment.getTransactionId())
                .paymentIntentId(savedPayment.getPaymentIntentId())
                .fromAccountId(savedPayment.getFromAccountId())
                .toAccountId(savedPayment.getToAccountId())
                .transactionAmount(savedPayment.getAmount())
                .customerId(savedPayment.getCustomerId())
                .currency(savedPayment.getCurrency())
                .build());
    }

}