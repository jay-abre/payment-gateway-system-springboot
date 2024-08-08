package com.electric_titans.accountservice.repository;

import com.electric_titans.accountservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
