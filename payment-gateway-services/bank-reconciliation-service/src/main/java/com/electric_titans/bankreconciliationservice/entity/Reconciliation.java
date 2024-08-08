package com.electric_titans.bankreconciliationservice.entity;

import com.electric_titans.bankreconciliationservice.enums.StatusEnum;
import com.electric_titans.common.enums.TransactionTypeEnum;
import com.electric_titans.bankreconciliationservice.enums.DiscrepancyTypeEnum;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reconciliations")
public class Reconciliation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reconciliationId;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "to_account_id")
    private Long toAccountId;

    @Column(name = "from_account_id")
    private Long fromAccountId;

    @Column(name = "transaction_id", unique = true)
    private Long transactionId;

    @Column(name = "stripe_id", unique = true, nullable = false)
    private String stripeId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @Column(name = "transaction_date")
    private Instant transactionDate;

    @Column(name = "transaction_amount")
    private Long transactionAmount;

    // nullable in case of failed payment intent
    @Column(name = "payment_amount")
    private Long paymentAmount;

    @Column(name = "transaction_currency")
    private String transactionCurrency;

    @Column(name = "payment_currency")
    private String paymentCurrency;

    @Column(name = "discrepancy_amount")
    private Long discrepancyAmount;

    @Column(name = "discrepancy_type")
    @Enumerated(EnumType.STRING)
    private DiscrepancyTypeEnum discrepancyType;

    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private TransactionTypeEnum transactionType;

    @Column(name = "completed_at")
    private Instant completedAt;
}