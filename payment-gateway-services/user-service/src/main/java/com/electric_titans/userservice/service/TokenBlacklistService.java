package com.electric_titans.userservice.service;

public interface TokenBlacklistService {

    void addToBlacklist(String email);

    boolean isBlacklisted(String email);
}
