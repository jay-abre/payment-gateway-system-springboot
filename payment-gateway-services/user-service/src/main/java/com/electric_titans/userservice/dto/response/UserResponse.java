package com.electric_titans.userservice.dto.response;

import com.electric_titans.userservice.enums.UserStatusEnum;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String firstName;
    private String middleName;
    private String lastName;
    private String mobileNumber;
    private String email;
    private UserStatusEnum status;
    private String customerId;
    private UserProfileResponse userProfile;
    private Instant createdAt;
    private Instant updatedAt;
}
