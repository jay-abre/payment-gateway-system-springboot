package com.electric_titans.common.event;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositApprovalEvent {
    private String status;
    private String transactionId;
}
