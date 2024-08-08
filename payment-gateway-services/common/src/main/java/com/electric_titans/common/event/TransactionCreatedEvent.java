package com.electric_titans.common.event;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionCreatedEvent {
    private Long transactionId;
    private Long fromAccountId;
    private Long toAccountId;
    private String customerId;
    private Long amount;
    private String currency;
    private String paymentMethod;
}
