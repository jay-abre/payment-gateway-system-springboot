package com.electric_titans.userservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    @Schema(example = "johnchristopherilacad27@gmail.com")
    private String email;

    @Schema(example = "Password123!")
    @NotBlank(message = "Password cannot be empty")
    private String password;
}
