package com.electric_titans.userservice.controller;

import com.electric_titans.userservice.dto.request.RegisterRequest;
import com.electric_titans.userservice.dto.request.VerifyKycRequest;
import com.electric_titans.userservice.dto.response.StatusResponse;
import com.electric_titans.userservice.dto.response.UserResponse;
import com.electric_titans.userservice.dto.response.UserPageResponse;
import com.electric_titans.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admins")
public class AdminController {

    private final UserService userService;

    @Operation(
            summary = "Create an administrator",
            description = "Creates a new administrator user. Requires SUPER_ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Administrator created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input")
    })
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping
    public ResponseEntity<UserResponse> createAdministrator(
            @RequestBody @Valid RegisterRequest registerRequest) {
        log.debug("createAdministrator({})", registerRequest.getEmail());
        return new ResponseEntity<>(userService.createAdministrator(registerRequest), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get all users",
            description = "Retrieves a paginated list of users with optional filters. Requires ADMIN or SUPER_ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping
    public ResponseEntity<UserPageResponse> getAllUsers(
            @RequestParam(value = "pageNo", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "id", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir,
            @RequestParam(value = "userStatus", required = false) String userStatus,
            @RequestParam(value = "kycStatus", required = false) String kycStatus) {
        log.debug("getAllUsers({}, {}, {}, {}, {}, {})", pageNo, pageSize, sortBy, sortDir, userStatus, kycStatus);
        return ResponseEntity.ok(userService.getAllUsers(pageNo, pageSize, sortBy, sortDir, userStatus, kycStatus));
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves a user by their ID. Requires ADMIN or SUPER_ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        log.debug("getUserById({})", userId);
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @Operation(
            summary = "Deactivate a user",
            description = "Deactivates a user by their ID. Requires ADMIN or SUPER_ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deactivated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long userId) {
        log.debug("deactivateUser({})", userId);
        userService.deactivateUser(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Check user status",
            description = "Checks the status of a user by their ID. Requires ADMIN or SUPER_ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User status retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/{userId}/status")
    public ResponseEntity<StatusResponse> checkStatusByUserId(@PathVariable Long userId) {
        log.debug("checkStatusByUserId({})", userId);
        return ResponseEntity.ok(userService.checkStatusByUserId(userId));
    }

    @Operation(
            summary = "Verify user KYC",
            description = "Verifies the KYC status of a user by their ID. Requires ADMIN or SUPER_ADMIN role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User KYC status verified successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @PutMapping("/{userId}/verify-kyc")
    public ResponseEntity<UserResponse> verifyUserKyc(
            @PathVariable Long userId,
            @RequestBody VerifyKycRequest verifyKycRequest) {
        log.debug("verifyUserKyc({}, {})", userId, verifyKycRequest.getKycStatusEnum().name());
        return ResponseEntity.ok(userService.verifyUserKyc(userId, verifyKycRequest));
    }
}
