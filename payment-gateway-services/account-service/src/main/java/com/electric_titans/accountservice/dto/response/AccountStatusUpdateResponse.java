package com.electric_titans.accountservice.dto.response;

import com.electric_titans.accountservice.enums.AccountStatusEnum;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatusUpdateResponse {
    private String message;
    private AccountStatusEnum status;
}
