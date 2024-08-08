package com.electric_titans.accountservice.dto.response;

import com.electric_titans.accountservice.enums.AccountStatusEnum;
import com.electric_titans.accountservice.enums.AccountTypeEnum;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {
    private Long id;
    private Long userId;
    private AccountTypeEnum accountType;
    private Long balance;
    private String currency;
    private AccountStatusEnum status;
    private Instant createdAt;
    private Instant updatedAt;
}
