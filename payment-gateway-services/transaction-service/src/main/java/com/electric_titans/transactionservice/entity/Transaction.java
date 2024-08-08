package com.electric_titans.transactionservice.entity;

import com.electric_titans.transactionservice.enums.StatusEnum;
import com.electric_titans.transactionservice.enums.TransactionTypeEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "from_account_id")
    private Long fromAccountId;

    @Column(name = "to_account_id")
    private Long toAccountId;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private TransactionTypeEnum transactionType;

    @Column(name = "description")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
