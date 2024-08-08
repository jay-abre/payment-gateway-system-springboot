package com.electric_titans.userservice.dto.response;

import com.electric_titans.userservice.enums.KycStatusEnum;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private Long id;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String postalCode;
    private KycStatusEnum kycStatus;
    private String idPicture;
    private Instant createdAt;
    private Instant updatedAt;
}
