package com.electric_titans.transactionservice.dto.request;

import com.electric_titans.transactionservice.enums.TransactionTypeEnum;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

    private Long fromAccountId;
    private Long toAccountId;
    private Long amount;
    private String currency;
    private String description;
    private TransactionTypeEnum transactionType;
}
