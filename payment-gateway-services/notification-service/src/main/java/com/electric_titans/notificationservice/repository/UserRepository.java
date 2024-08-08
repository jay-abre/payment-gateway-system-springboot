package com.electric_titans.notificationservice.repository;

import com.electric_titans.notificationservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>{

    Optional<User> findByCustomerId(String customerId);

}
