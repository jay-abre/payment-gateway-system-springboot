package com.electric_titans.common.event;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountCreatedEvent {
    String email;
    String country;
}
