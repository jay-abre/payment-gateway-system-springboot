package com.electric_titans.userservice.controller;

import com.electric_titans.userservice.dto.response.UserResponse;
import com.electric_titans.userservice.dto.request.RegisterRequest;
import com.electric_titans.userservice.dto.request.UserProfileRequest;
import com.electric_titans.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Schema;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@PreAuthorize("isAuthenticated()")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Get Authenticated User",
            description = "Retrieves the details of the currently authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the authenticated user's details"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - The user is not authenticated")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getAuthenticatedUser() {
        log.debug("getAuthenticatedUser()");
        return ResponseEntity.ok(userService.getAuthenticatedUser());
    }

    @Operation(
            summary = "Update User",
            description = "Updates the user details for the specified user ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully updated"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody @Valid RegisterRequest registerRequest) {
        log.debug("updateUser({}, {})", id, registerRequest.getEmail());
        return ResponseEntity.ok(userService.updateUser(id, registerRequest));
    }

    @Operation(
            summary = "Update Contact Information",
            description = "Updates the contact information and profile picture for the specified user ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Contact information successfully updated"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "415", description = "Unsupported Media Type - Invalid file format")
    })
    @PutMapping("/{userId}/contact-info")
    public ResponseEntity<UserResponse> updateContactInformation(
            @PathVariable Long userId,
            @RequestParam("idPicture") @Schema(type = "string", format = "binary") MultipartFile idPicture,
            @RequestPart("userProfileRequest") UserProfileRequest userProfileRequest) {
        log.debug("updateContactInformation({}, {}, {})", userId, userProfileRequest.getClass().getName(), idPicture.getContentType());
        return ResponseEntity.ok(userService.updateContactInformation(userId, userProfileRequest, idPicture));
    }

    @Operation(
            summary = "Retrieve KYC Status",
            description = "Retrieve the KYC status of the user and verify whether it is fully verified."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the KYC Status"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input"),
            @ApiResponse(responseCode = "404", description = "User not found"),
    })
    @PostMapping("/{userId}/kyc-status")
    public Mono<Boolean> getKycStatus(@PathVariable Long userId) {
        return Mono.just(userService.getKycStatus(userId));
    }
}