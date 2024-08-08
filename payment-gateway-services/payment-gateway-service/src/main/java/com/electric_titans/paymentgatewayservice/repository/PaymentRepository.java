package com.electric_titans.paymentgatewayservice.repository;

import com.electric_titans.paymentgatewayservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByPaymentMethodId(String paymentMethodId);
}