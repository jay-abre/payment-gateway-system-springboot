package com.electric_titans.common.event;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEvent {
    private String name;
    private String email;
    private String customerId;
}
