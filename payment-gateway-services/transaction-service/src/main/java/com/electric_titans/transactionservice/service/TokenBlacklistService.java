package com.electric_titans.transactionservice.service;

public interface TokenBlacklistService {
    boolean isBlacklisted(String userEmail);
}
