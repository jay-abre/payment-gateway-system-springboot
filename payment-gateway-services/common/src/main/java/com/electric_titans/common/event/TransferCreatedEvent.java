package com.electric_titans.common.event;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferCreatedEvent {
    private String currency;
    private Long amount;
    private String toAccountId;
}
