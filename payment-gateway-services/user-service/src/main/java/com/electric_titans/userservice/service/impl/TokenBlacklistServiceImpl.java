package com.electric_titans.userservice.service.impl;

import com.electric_titans.userservice.entity.User;
import com.electric_titans.userservice.exception.ResourceNotFoundException;
import com.electric_titans.userservice.repository.UserRepository;
import com.electric_titans.userservice.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl implements TokenBlacklistService {

    private final UserRepository userRepository;

    @Override
    public void addToBlacklist(String email) {
        log.debug("addToBlacklist({})", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        user.setBlacklisted(true);
        userRepository.save(user);
        log.info("User blacklisted");
    }

    @Override
    public boolean isBlacklisted(String email) {
        log.debug("isBlacklisted({})", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        return user.isBlacklisted();
    }
}
