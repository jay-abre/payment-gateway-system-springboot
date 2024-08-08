package com.electric_titans.bankreconciliationservice.service;

public interface TokenBlacklistService {
    boolean isBlacklisted(String userEmail);
}

