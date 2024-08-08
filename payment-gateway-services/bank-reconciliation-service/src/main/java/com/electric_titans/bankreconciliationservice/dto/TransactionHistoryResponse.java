package com.electric_titans.bankreconciliationservice.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionHistoryResponse {
    private List<StripeTransaction> transactions;
    private boolean hasMore;
    private String nextPageUrl;
}
