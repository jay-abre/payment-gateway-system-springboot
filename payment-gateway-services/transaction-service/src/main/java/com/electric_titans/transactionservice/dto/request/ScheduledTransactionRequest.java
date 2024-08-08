package com.electric_titans.transactionservice.dto.request;

import com.electric_titans.transactionservice.enums.TransactionTypeEnum;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledTransactionRequest {
    private Long fromAccountId;
    private Long toAccountId;
    private Long amount;
    private String currency;
    private String description;
    private TransactionTypeEnum transactionType;
    private Instant scheduledFor;
}
