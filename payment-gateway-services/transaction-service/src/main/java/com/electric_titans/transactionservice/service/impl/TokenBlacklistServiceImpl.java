package com.electric_titans.transactionservice.service.impl;

import com.electric_titans.transactionservice.entity.User;
import com.electric_titans.transactionservice.repository.UserRepository;
import com.electric_titans.transactionservice.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final UserRepository userRepository;

    @Override
    public boolean isBlacklisted(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User"));

        return user.isBlacklisted();
    }
}
