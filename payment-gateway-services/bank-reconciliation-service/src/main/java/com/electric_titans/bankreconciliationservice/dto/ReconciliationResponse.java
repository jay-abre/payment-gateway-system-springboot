package com.electric_titans.bankreconciliationservice.dto;

import com.electric_titans.bankreconciliationservice.enums.DiscrepancyTypeEnum;
import com.electric_titans.bankreconciliationservice.enums.StatusEnum;
import com.electric_titans.common.enums.TransactionTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReconciliationResponse {
    @NotNull
    private Long reconciliationId;

    @NotNull
    private String customerId;

    // @NotNull
    private TransactionTypeEnum transactionType;

    @NotNull
    private String stripeId;

    private StatusEnum status;

    private Instant transactionDate;

    private Long transactionAmount;

    private Long paymentAmount;

    private String transactionCurrency;

    private String paymentCurrency;

    private Long discrepancyAmount;

    private DiscrepancyTypeEnum discrepancyType;

    private Instant completedAt;

}
