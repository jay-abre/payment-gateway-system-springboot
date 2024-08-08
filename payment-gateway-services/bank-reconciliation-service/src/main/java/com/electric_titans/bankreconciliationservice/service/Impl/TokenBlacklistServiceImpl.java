package com.electric_titans.bankreconciliationservice.service.Impl;

import com.electric_titans.bankreconciliationservice.entity.User;
import com.electric_titans.bankreconciliationservice.exception.ResourceNotFoundException;
import com.electric_titans.bankreconciliationservice.repository.UserRepository;
import com.electric_titans.bankreconciliationservice.service.TokenBlacklistService;
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
                .orElseThrow(() -> new ResourceNotFoundException("User", "email",
                        userEmail));

        return user.isBlacklisted();
    }
}
