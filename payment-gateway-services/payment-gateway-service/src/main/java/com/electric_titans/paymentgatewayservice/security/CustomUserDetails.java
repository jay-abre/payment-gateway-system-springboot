package com.electric_titans.paymentgatewayservice.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface CustomUserDetails extends UserDetails {
    String getEmail();
}