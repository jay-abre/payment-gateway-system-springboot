package com.electric_titans.transactionservice.dto.response;

import com.electric_titans.transactionservice.enums.StatusEnum;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionStatusResponse {

    private Long transactionId;
    private StatusEnum status;
    private Instant lastUpdated;
}
