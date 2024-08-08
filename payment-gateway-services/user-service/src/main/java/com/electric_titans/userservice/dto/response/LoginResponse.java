package com.electric_titans.userservice.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String userId;
    private String token;
    private String expiresAt;
}
