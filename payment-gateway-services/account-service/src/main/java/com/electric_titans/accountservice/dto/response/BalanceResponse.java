package com.electric_titans.accountservice.dto.response;

import lombok.*;

import java.math.BigDecimal;
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
