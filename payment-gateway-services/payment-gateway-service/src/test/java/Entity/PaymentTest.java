package Entity;

import com.electric_titans.paymentgatewayservice.entity.Payment;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentTest {

    @Test
    void testPaymentEntityCreation() {
        UUID id = UUID.randomUUID();
        String paymentIntentId = "pi_123456789";
        Long amount = 1000L;
        String customerId = "cus_123456789";
        Long transactionId = 123456789L;
        Long fromAccountId = 987654321L;
        Long toAccountId = 123456789L;
        String currency = "USD";
        String status = "succeeded";
        String paymentMethodId = "pm_123456789";
        ZonedDateTime createdAt = ZonedDateTime.now();
        ZonedDateTime updatedAt = ZonedDateTime.now();

        Payment payment = Payment.builder()
                .id(id)
                .paymentIntentId(paymentIntentId)
                .amount(amount)
                .customerId(customerId)
                .transactionId(transactionId)
                .fromAccountId(fromAccountId)
                .toAccountId(toAccountId)
                .currency(currency)
                .status(status)
                .paymentMethodId(paymentMethodId)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        assertNotNull(payment);
        assertEquals(id, payment.getId());
        assertEquals(paymentIntentId, payment.getPaymentIntentId());
        assertEquals(amount, payment.getAmount());
        assertEquals(customerId, payment.getCustomerId());
        assertEquals(transactionId, payment.getTransactionId());
        assertEquals(fromAccountId, payment.getFromAccountId());
        assertEquals(toAccountId, payment.getToAccountId());
        assertEquals(currency, payment.getCurrency());
        assertEquals(status, payment.getStatus());
        assertEquals(paymentMethodId, payment.getPaymentMethodId());
        assertEquals(createdAt, payment.getCreatedAt());
        assertEquals(updatedAt, payment.getUpdatedAt());
    }

    @Test
    void testPaymentEntitySettersAndGetters() {
        Payment payment = new Payment();
        UUID id = UUID.randomUUID();
        String paymentIntentId = "pi_123456789";
        Long amount = 1000L;
        String customerId = "cus_123456789";
        Long transactionId = 123456789L;
        Long fromAccountId = 987654321L;
        Long toAccountId = 123456789L;
        String currency = "USD";
        String status = "succeeded";
        String paymentMethodId = "pm_123456789";
        ZonedDateTime createdAt = ZonedDateTime.now();
        ZonedDateTime updatedAt = ZonedDateTime.now();

        payment.setId(id);
        payment.setPaymentIntentId(paymentIntentId);
        payment.setAmount(amount);
        payment.setCustomerId(customerId);
        payment.setTransactionId(transactionId);
        payment.setFromAccountId(fromAccountId);
        payment.setToAccountId(toAccountId);
        payment.setCurrency(currency);
        payment.setStatus(status);
        payment.setPaymentMethodId(paymentMethodId);
        payment.setCreatedAt(createdAt);
        payment.setUpdatedAt(updatedAt);

        assertEquals(id, payment.getId());
        assertEquals(paymentIntentId, payment.getPaymentIntentId());
        assertEquals(amount, payment.getAmount());
        assertEquals(customerId, payment.getCustomerId());
        assertEquals(transactionId, payment.getTransactionId());
        assertEquals(fromAccountId, payment.getFromAccountId());
        assertEquals(toAccountId, payment.getToAccountId());
        assertEquals(currency, payment.getCurrency());
        assertEquals(status, payment.getStatus());
        assertEquals(paymentMethodId, payment.getPaymentMethodId());
        assertEquals(createdAt, payment.getCreatedAt());
        assertEquals(updatedAt, payment.getUpdatedAt());
    }
}