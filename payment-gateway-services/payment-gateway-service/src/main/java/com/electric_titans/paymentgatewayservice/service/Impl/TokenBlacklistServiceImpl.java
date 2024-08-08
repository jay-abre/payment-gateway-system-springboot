package com.electric_titans.paymentgatewayservice.service.Impl;

import com.electric_titans.paymentgatewayservice.entity.User;
import com.electric_titans.paymentgatewayservice.exception.ResourceNotFoundException;
import com.electric_titans.paymentgatewayservice.repository.UserRepository;
import com.electric_titans.paymentgatewayservice.service.TokenBlacklistService;
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
