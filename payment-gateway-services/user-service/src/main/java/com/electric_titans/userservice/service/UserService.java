package com.electric_titans.userservice.service;

import com.electric_titans.userservice.dto.request.RegisterRequest;
import com.electric_titans.userservice.dto.request.UserProfileRequest;
import com.electric_titans.userservice.dto.request.VerifyKycRequest;
import com.electric_titans.userservice.dto.response.StatusResponse;
import com.electric_titans.userservice.dto.response.UserPageResponse;
import com.electric_titans.userservice.dto.response.UserResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserResponse getAuthenticatedUser();

    UserPageResponse getAllUsers(int pageNo, int pageSize, String sortBy, String sortDir, String userStatus, String kycStatus);

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, RegisterRequest registerRequest);

    void deactivateUser(Long id);

    StatusResponse checkStatusByUserId(Long id);

    UserResponse updateContactInformation(Long userId, UserProfileRequest userProfileRequest, MultipartFile idPicture);

    UserResponse createAdministrator(RegisterRequest registerRequest);

    UserResponse verifyUserKyc(Long userId, VerifyKycRequest verifyKycRequest);

    boolean getKycStatus(Long userId);
}