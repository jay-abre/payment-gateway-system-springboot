package com.electric_titans.userservice.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface CustomUserDetails extends UserDetails {

    String getEmail();
}
