package com.electric_titans.common.event;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentSuccessEvent {
    private String paymentId;
    private String paymentIntentId;
    private Long transactionId;
    private Long fromAccountId;
    private Long toAccountId;
    private Long transactionAmount;
    private String customerId;
    private String currency;
}
