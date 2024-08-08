package com.electric_titans.userservice.repository;

import com.electric_titans.userservice.entity.User;
import com.electric_titans.userservice.enums.KycStatusEnum;
import com.electric_titans.userservice.enums.UserStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<User> findByStatus(UserStatusEnum statusEnum, Pageable pageable);

    Page<User> findByUserProfile_kycStatus(KycStatusEnum kycStatusEnum, Pageable pageable);
}