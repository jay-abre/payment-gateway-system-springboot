package com.electric_titans.transactionservice.dto.response;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BalanceResponse {
    private Long accountId;
    private Long balance;
    private String currency;
    private Instant lastUpdated;
}