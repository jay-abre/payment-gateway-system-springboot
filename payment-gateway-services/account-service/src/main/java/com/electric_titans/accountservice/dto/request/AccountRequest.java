package com.electric_titans.accountservice.dto.request;

import com.electric_titans.accountservice.enums.AccountStatusEnum;
import com.electric_titans.accountservice.enums.AccountTypeEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequest {

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotNull(message = "Account type cannot be null")
    private AccountTypeEnum accountType;

    @PositiveOrZero(message = "Balance must be positive or zero")
    private Long balance;

    @Size(min = 3, max = 3, message = "Currency code must be 3 characters")
    private String currency;

    @NotNull(message = "Account status cannot be null")
    private AccountStatusEnum status;
}
