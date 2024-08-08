package com.electric_titans.bankreconciliationservice.repository;

import com.electric_titans.bankreconciliationservice.entity.Reconciliation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReconciliationRepository extends JpaRepository<Reconciliation, Long> {
    Reconciliation save(Reconciliation reconciliation);
    Optional<Reconciliation> findByStripeId(String stripeId);
}
