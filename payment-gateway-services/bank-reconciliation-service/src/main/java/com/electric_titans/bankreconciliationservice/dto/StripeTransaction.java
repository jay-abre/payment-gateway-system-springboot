package com.electric_titans.bankreconciliationservice.dto;

import lombok.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StripeTransaction {
    private String id;
    private Long amount;
    private Long created;
    private String description;
    private Long fee;
    private Long net;
    private String status;
    private String type;
    private String currency;
    private String sourceId;
}
