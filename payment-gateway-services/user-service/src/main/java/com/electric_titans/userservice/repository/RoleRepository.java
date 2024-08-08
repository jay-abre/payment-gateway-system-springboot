package com.electric_titans.userservice.repository;

import com.electric_titans.userservice.entity.Role;
import com.electric_titans.userservice.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleEnum roleEnum);
}
