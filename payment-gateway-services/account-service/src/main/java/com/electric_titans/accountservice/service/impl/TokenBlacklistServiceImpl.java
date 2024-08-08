package com.electric_titans.accountservice.service.impl;

import com.electric_titans.accountservice.entity.User;
import com.electric_titans.accountservice.exception.ResourceNotFoundException;
import com.electric_titans.accountservice.repository.UserRepository;
import com.electric_titans.accountservice.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final UserRepository userRepository;

    @Override
    public boolean isBlacklisted(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", userEmail));

        return user.isBlacklisted();
    }
}
