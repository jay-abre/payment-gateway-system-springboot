package com.electric_titans.userservice.service;

import com.electric_titans.userservice.dto.response.LoginResponse;
import com.electric_titans.userservice.dto.request.LoginRequest;
import com.electric_titans.userservice.dto.request.RegisterRequest;
import com.electric_titans.userservice.dto.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {

    UserResponse signup(RegisterRequest registerRequest);

    LoginResponse authenticate(LoginRequest input);

    String logout(HttpServletRequest httpServletRequest);
}
