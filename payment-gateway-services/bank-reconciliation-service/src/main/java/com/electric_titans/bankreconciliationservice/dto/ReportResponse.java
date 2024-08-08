package com.electric_titans.bankreconciliationservice.dto;

import com.electric_titans.bankreconciliationservice.entity.Reconciliation;
import com.electric_titans.bankreconciliationservice.enums.StatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {

    @NotNull
    private Instant generatedAt;

    @NotNull
    private Instant startDate;

    @NotNull
    private Instant endDate;

    @NotNull
    private Integer totalTransactions;

    @NotNull
    private Integer matchedTransactions;

    @NotNull
    private Integer unmatchedTransactions;

    @NotNull
    private Integer failedTransactions;

    @NotNull
    private Long totalAmountStripe;

    @NotNull
    private Long totalAmountInternal;

    @NotNull
    private Long discrepancyAmount;

    private List<ReconciliationResponse> unmatchedReconciliations;

    private List<ReconciliationResponse> failedReconciliations;
}
