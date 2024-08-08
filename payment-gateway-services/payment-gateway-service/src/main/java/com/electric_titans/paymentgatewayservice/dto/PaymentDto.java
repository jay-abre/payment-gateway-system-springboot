package com.electric_titans.paymentgatewayservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDto {
        private String customerId;
        private String cardNumber;
        private String cardCvc;
        private Long cardExpMonth;
        private Long cardExpYear;
}
