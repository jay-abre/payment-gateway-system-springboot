package com.electric_titans.userservice.dto.request;

import com.electric_titans.userservice.enums.KycStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VerifyKycRequest {

    @Schema(example = "FULLY_VERIFIED")
    @NotNull(message = "Kyc status cannot be null")
    private KycStatusEnum kycStatusEnum;
}
