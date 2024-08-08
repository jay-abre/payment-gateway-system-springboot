package com.electric_titans.paymentgatewayservice.repository;

import com.electric_titans.paymentgatewayservice.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}