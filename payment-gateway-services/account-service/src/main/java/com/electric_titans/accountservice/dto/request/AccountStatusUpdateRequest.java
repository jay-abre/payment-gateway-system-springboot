package com.electric_titans.accountservice.dto.request;

import com.electric_titans.accountservice.enums.AccountStatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatusUpdateRequest {

    @NotNull(message = "Account status cannot be null")
    private AccountStatusEnum status;
}
