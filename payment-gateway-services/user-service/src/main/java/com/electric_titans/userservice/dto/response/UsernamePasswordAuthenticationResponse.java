package com.electric_titans.userservice.dto.response;

import lombok.*;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsernamePasswordAuthenticationResponse {
    private boolean tokenValid;
}
