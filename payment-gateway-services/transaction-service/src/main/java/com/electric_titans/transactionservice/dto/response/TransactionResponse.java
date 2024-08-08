package com.electric_titans.transactionservice.dto.response;

import com.electric_titans.transactionservice.enums.StatusEnum;
import com.electric_titans.transactionservice.enums.TransactionTypeEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long transactionId;
    private Long fromAccountId;
    private Long toAccountId;
    private Long amount;
    private String currency;
    private StatusEnum status;
    private TransactionTypeEnum transactionType;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
}
