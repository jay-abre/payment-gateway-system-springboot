package com.electric_titans.common.event;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReconciliationEvent {
    private String status; // RECONCILED & FINISHED_WITH_DISCREPANCY, FAILED
    private String customerId;
    private Long amount;
    private String currency;
    private Instant date;
}
