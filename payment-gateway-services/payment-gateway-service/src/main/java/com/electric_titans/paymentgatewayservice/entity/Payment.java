package com.electric_titans.paymentgatewayservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

//    @Column(name = "user_id", nullable = false)
//    private UUID userId;

    @Column(name = "payment_intent_id", nullable = false)
    private String paymentIntentId;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "customerId", nullable = false)
    private String customerId;

    @Column(name = "transaction_id", nullable = false)
    private Long transactionId;

    @Column (name = "from_account_id", nullable = false)
    private Long fromAccountId;

    @Column (name = "to_account_id")
    private Long toAccountId;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "status" , nullable = false)
    private String status;

    @Column(name = "payment_method_id", nullable = false)
    private String paymentMethodId;

    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt = ZonedDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt = ZonedDateTime.now();

}