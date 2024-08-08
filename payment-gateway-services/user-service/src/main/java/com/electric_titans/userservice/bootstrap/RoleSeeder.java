package com.electric_titans.userservice.bootstrap;

import com.electric_titans.userservice.entity.Role;
import com.electric_titans.userservice.enums.RoleEnum;
import com.electric_titans.userservice.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

@Slf4j
@Order(1)
@Component
@RequiredArgsConstructor
public class RoleSeeder implements ApplicationListener<ContextRefreshedEvent> {

    private final RoleRepository roleRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.debug("onApplicationEvent({})", event.toString());
        this.loadRoles();
    }

    private void loadRoles() {

        RoleEnum[] roleEnums = new RoleEnum[]{RoleEnum.USER, RoleEnum.ADMIN, RoleEnum.SUPER_ADMIN};
        Map<RoleEnum, String> roleDescription = Map.of(
                RoleEnum.USER, "Default User Role",
                RoleEnum.ADMIN, "Administrator Role",
                RoleEnum.SUPER_ADMIN, "Super Administrator Role"
        );

        Arrays.stream(roleEnums).forEach(roleEnum -> {
            boolean isRoleEmpty = roleRepository.findByName(roleEnum)
                    .isEmpty();

            if (isRoleEmpty) {
                Role role = new Role();
                role.setName(roleEnum);
                role.setDescription(roleDescription.get(roleEnum));
                roleRepository.save(role);
                log.info("Role with name: {} created", role.getName());
            }
        });
    }
}
