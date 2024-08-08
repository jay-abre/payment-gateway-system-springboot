package com.electric_titans.bankreconciliationservice.dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private Instant timestamp;
    private String message;
    private String path;
}
