package com.electric_titans.userservice.service.impl;

import com.electric_titans.userservice.dto.request.RegisterRequest;
import com.electric_titans.userservice.dto.request.UserProfileRequest;
import com.electric_titans.userservice.dto.request.VerifyKycRequest;
import com.electric_titans.userservice.dto.response.StatusResponse;
import com.electric_titans.userservice.dto.response.UserPageResponse;
import com.electric_titans.userservice.dto.response.UserProfileResponse;
import com.electric_titans.userservice.dto.response.UserResponse;
import com.electric_titans.userservice.entity.Role;
import com.electric_titans.userservice.entity.User;
import com.electric_titans.userservice.entity.UserProfile;
import com.electric_titans.userservice.enums.KycStatusEnum;
import com.electric_titans.userservice.enums.RoleEnum;
import com.electric_titans.userservice.enums.UserStatusEnum;
import com.electric_titans.userservice.exception.ImageProcessingException;
import com.electric_titans.userservice.exception.ResourceNotFoundException;
import com.electric_titans.userservice.exception.StripeCustomerException;
import com.electric_titans.userservice.exception.UserAlreadyExistsException;
import com.electric_titans.userservice.mapper.UserMapper;
import com.electric_titans.userservice.mapper.UserProfileMapper;
import com.electric_titans.userservice.repository.RoleRepository;
import com.electric_titans.userservice.repository.UserProfileRepository;
import com.electric_titans.userservice.repository.UserRepository;
import com.electric_titans.userservice.service.UserService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerUpdateParams;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Override
    public UserResponse getAuthenticatedUser() {
        log.debug("getAuthenticatedUser()");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        log.info("User found with email: {}", currentUser.getEmail());
        return UserMapper.INSTANCE.userToUserResponse(currentUser);
    }

    @Override
    public UserPageResponse getAllUsers(int pageNo, int pageSize, String sortBy, String sortDir, String userStatus, String kycStatus) {
        log.debug("getAllUsers({}, {}, {}, {}, {}, {})", pageNo, pageSize, sortBy, sortDir, userStatus, kycStatus);
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        Page<User> users;
        if (userStatus != null)
            users = userRepository.findByStatus(UserStatusEnum.valueOf(userStatus.toUpperCase()), pageable);
        else if (kycStatus != null)
            users = userRepository.findByUserProfile_kycStatus(KycStatusEnum.valueOf(kycStatus.toUpperCase()), pageable);
        else users = userRepository.findAll(pageable);

        List<UserResponse> content = users.getContent()
                .stream()
                .map(UserMapper.INSTANCE::userToUserResponse)
                .toList();

        return UserPageResponse.builder()
                .content(content)
                .pageNo(users.getNumber())
                .pageSize(users.getSize())
                .totalElements(users.getTotalElements())
                .totalPages(users.getTotalPages())
                .last(users.isLast())
                .build();
    }

    @Override
    @Transactional
    public UserResponse createAdministrator(RegisterRequest registerRequest) {
        log.debug("createAdministrator({})", registerRequest.getEmail());
        boolean isUserExists = userRepository.existsByEmail(registerRequest.getEmail());
        if (isUserExists) {
            throw new UserAlreadyExistsException(registerRequest.getEmail());
        }

        Role role = roleRepository.findByName(RoleEnum.ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", RoleEnum.ADMIN.name()));

        User user = UserMapper.INSTANCE.registerRequestToUser(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setStatus(UserStatusEnum.ACTIVE);
        user.setBlacklisted(true);
        user.setRole(role);
        User savedUser = userRepository.save(user);
        log.info("User registered with email: {}", savedUser.getEmail());
        return UserMapper.INSTANCE.userToUserResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(Long id) {
        log.debug("getUserById({})", id);
        return userRepository.findById(id)
                .map(UserMapper.INSTANCE::userToUserResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id.toString()));
    }

    @Override
    public UserResponse updateUser(Long id, RegisterRequest registerRequest) {
        log.debug("updateUser({}, {})", id, registerRequest.getEmail());
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id.toString()));

        String customerId = user.getCustomerId();
        if (customerId == null) {
            throw new ResourceNotFoundException("Customer id not found.");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new UserAlreadyExistsException(registerRequest.getEmail());
        }

        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setFirstName(registerRequest.getFirstName());
        user.setMiddleName(registerRequest.getMiddleName());
        user.setLastName(registerRequest.getLastName());
        user.setMobileNumber(registerRequest.getMobileNumber());
        User updatedUser = userRepository.save(user);
        String middleName = updatedUser.getMiddleName() == null ? "" : updatedUser.getMiddleName().concat(" ");
        String fullName = updatedUser.getFirstName().concat(" ").concat(middleName).concat(updatedUser.getLastName());
        Stripe.apiKey = stripeApiKey;
        Customer customer;
        try {
            customer = Customer.retrieve(customerId);
        } catch (StripeException e) {
            throw new StripeCustomerException("retrieve", customerId);
        }

        CustomerUpdateParams params = CustomerUpdateParams.builder()
                .setEmail(updatedUser.getEmail())
                .setName(fullName)
                .build();

        try {
            customer.update(params);
        } catch (StripeException e) {
            throw new StripeCustomerException("update", customerId);
        }

        log.info("User updated with email: {}", updatedUser.getEmail());
        return UserMapper.INSTANCE.userToUserResponse(updatedUser);
    }

    @Override
    public void deactivateUser(Long id) {
        log.debug("deactivateUser({})", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id.toString()));

        user.setStatus(UserStatusEnum.INACTIVE);
        userRepository.save(user);
        log.info("User deactivated with email: {}", user.getEmail());
    }

    @Override
    public StatusResponse checkStatusByUserId(Long id) {
        log.debug("checkStatusByUserId({})", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id.toString()));

        StatusResponse statusResponse = new StatusResponse();
        statusResponse.setStatus(user.getStatus());
        log.info("User with email: {} has {} status.", user.getEmail(), statusResponse.getStatus());
        return statusResponse;
    }

    @Transactional
    @Override
    public UserResponse updateContactInformation(Long userId, UserProfileRequest userProfileRequest, MultipartFile idPicture) {
        log.debug("updateContactInfo({}, {})", userId, userProfileRequest.getClass());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));

        String customerId = user.getCustomerId();
        if (customerId == null) {
            throw new ResourceNotFoundException("Customer id not found.");
        }

        UserProfile userProfile = user.getUserProfile();
        UserProfile savedUserProfile;
        String base64Image;
        try {
            base64Image = Base64.getEncoder().encodeToString(idPicture.getBytes());
        } catch (IOException e) {
            throw new ImageProcessingException("Failed to process image.");
        }
        String imageType = idPicture.getContentType();
        String dataUri = "data:" + imageType + ";base64," + base64Image;
        if (userProfile == null) {
            UserProfile newUserProfile = UserProfileMapper.INSTANCE.userProfileRequestToUserProfile(userProfileRequest);
            newUserProfile.setUser(user);
            newUserProfile.setIdPicture(dataUri);
            newUserProfile.setKycStatus(KycStatusEnum.PENDING);
            savedUserProfile = userProfileRepository.save(newUserProfile);
        } else {
            userProfile.setAddressLine1(userProfileRequest.getAddressLine1());
            userProfile.setAddressLine2(userProfileRequest.getAddressLine2());
            userProfile.setCity(userProfileRequest.getCity());
            userProfile.setState(userProfileRequest.getState());
            userProfile.setCountry(userProfileRequest.getCountry());
            userProfile.setPostalCode(userProfileRequest.getPostalCode());
            userProfile.setKycStatus(KycStatusEnum.PENDING);
            userProfile.setIdPicture(dataUri);
            savedUserProfile = userProfileRepository.save(userProfile);
        }

        Stripe.apiKey = stripeApiKey;
        Customer customer;
        try {
            customer = Customer.retrieve(customerId);
        } catch (StripeException e) {
            throw new StripeCustomerException("retrieve", customerId);
        }

        Map<String, Object> addressMap = new HashMap<>();
        addressMap.put("city", savedUserProfile.getCity());
        addressMap.put("country", savedUserProfile.getCountry());
        addressMap.put("line1", savedUserProfile.getAddressLine1());
        addressMap.put("line2", savedUserProfile.getAddressLine2());
        addressMap.put("postal_code", savedUserProfile.getPostalCode());
        addressMap.put("state", savedUserProfile.getState());
        CustomerUpdateParams params = CustomerUpdateParams.builder()
                .putExtraParam("address", addressMap)
                .build();

        try {
            customer.update(params);
        } catch (StripeException e) {
            throw new StripeCustomerException("update", customerId);
        }

        log.info("User profile updated with id : {}", savedUserProfile.getId());
        UserProfileResponse userProfileResponse = UserProfileMapper.INSTANCE.userProfileToUserProfileResponse(savedUserProfile);
        UserResponse userResponse = UserMapper.INSTANCE.userToUserResponse(user);
        userResponse.setUserProfile(userProfileResponse);
        return userResponse;
    }

    @Override
    public UserResponse verifyUserKyc(Long userId, VerifyKycRequest verifyKycRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));

        UserProfile userProfile = user.getUserProfile();
        if (userProfile == null) {
            throw new ResourceNotFoundException("UserProfile", "id", userProfile.getId().toString());
        }

        userProfile.setKycStatus(verifyKycRequest.getKycStatusEnum());
        UserProfile updatedUserProfile = userProfileRepository.save(userProfile);
        UserProfileResponse userProfileResponse = UserProfileMapper.INSTANCE.userProfileToUserProfileResponse(updatedUserProfile);
        UserResponse userResponse = UserMapper.INSTANCE.userToUserResponse(user);
        userResponse.setUserProfile(userProfileResponse);
        return userResponse;
    }

    @Override
    public boolean getKycStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));

        UserProfile userProfile = user.getUserProfile();
        if (userProfile == null) {
            throw new ResourceNotFoundException("UserProfile", "id", userProfile.getId().toString());
        }

        if (userProfile.getKycStatus().name().equalsIgnoreCase("FULLY_VERIFIED")) return true;
        return false;
    }
}