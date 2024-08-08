package com.electric_titans.bankreconciliationservice.repository;

import com.electric_titans.bankreconciliationservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}