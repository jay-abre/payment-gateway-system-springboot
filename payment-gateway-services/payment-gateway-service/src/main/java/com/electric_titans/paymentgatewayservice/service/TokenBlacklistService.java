package com.electric_titans.paymentgatewayservice.service;

public interface TokenBlacklistService {
    boolean isBlacklisted(String userEmail);
}

