package com.electric_titans.userservice.repository;

import com.electric_titans.userservice.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
