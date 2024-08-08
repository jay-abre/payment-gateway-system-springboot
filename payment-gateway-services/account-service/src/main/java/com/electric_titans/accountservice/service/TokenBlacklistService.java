package com.electric_titans.accountservice.service;

public interface TokenBlacklistService {

    boolean isBlacklisted(String userEmail);
}
