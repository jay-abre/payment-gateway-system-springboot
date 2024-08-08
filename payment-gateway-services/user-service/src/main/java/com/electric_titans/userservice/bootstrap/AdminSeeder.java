package com.electric_titans.userservice.bootstrap;

import com.electric_titans.userservice.entity.Role;
import com.electric_titans.userservice.entity.User;
import com.electric_titans.userservice.enums.RoleEnum;
import com.electric_titans.userservice.enums.UserStatusEnum;
import com.electric_titans.userservice.exception.ResourceNotFoundException;
import com.electric_titans.userservice.repository.RoleRepository;
import com.electric_titans.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSeeder implements ApplicationListener<ContextRefreshedEvent> {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${super-admin.username}")
    private String username;

    @Value("${super-admin.email}")
    private String email;

    @Value("${super-admin.first-name}")
    private String firstName;

    @Value("${super-admin.middle-name}")
    private String middleName;

    @Value("${super-admin.last-name}")
    private String lastName;

    @Value("${super-admin.mobile-number}")
    private String mobileNumber;

    @Value("${super-admin.password}")
    private String password;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.debug("onApplicationEvent({})", event.toString());
        this.createSuperAdministrator();
    }

    private void createSuperAdministrator() {
        boolean isUserExists = userRepository.existsByEmail(email);
        if (!isUserExists) {
            Role role = roleRepository.findByName(RoleEnum.SUPER_ADMIN)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "name", RoleEnum.SUPER_ADMIN.name()));

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setMiddleName(middleName);
            user.setLastName(lastName);
            user.setMobileNumber(mobileNumber);
            user.setPassword(passwordEncoder.encode(password));
            user.setBlacklisted(true);
            user.setStatus(UserStatusEnum.ACTIVE);
            user.setRole(role);
            userRepository.save(user);
            log.info("Super administrator with email: {} created", user.getEmail());
        }
    }
}
