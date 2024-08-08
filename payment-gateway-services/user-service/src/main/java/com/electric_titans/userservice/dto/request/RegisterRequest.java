package com.electric_titans.userservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @Schema(example = "jcilacad")
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @Schema(example = "johnchristopherilacad27@gmail.com")
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    @Schema(example = "John")
    @NotBlank(message = "First name cannot be empty")
    private String firstName;

    private String middleName;

    @Schema(example = "Ilacad")
    @NotBlank(message = "Last name cannot be empty")
    private String lastName;

    @Schema(example = "+6391234567891")
    @NotBlank(message = "Mobile number cannot be empty")
    @Pattern(regexp = "(^$|\\+\\d{1,3}\\s?\\d{1,14})",
            message = "Invalid contact number. Please provide a valid mobile number.")
    private String mobileNumber;

    @Schema(example = "Password123!")
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$",
            message = "Password must contain at least one lowercase letter, one uppercase letter, and one digit")
    private String password;
}
