package com.electric_titans.accountservice.controller;

import com.electric_titans.accountservice.dto.response.AccountResponse;
import com.electric_titans.accountservice.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final AccountService accountService;

    @Operation(
            summary = "Retrieve all accounts for a specific user",
            description = "Fetches a list of all accounts associated with the specified user ID. Requires ADMIN or USER role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of accounts retrieved successfully."),
})
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{userId}/accounts")
    public ResponseEntity<List<AccountResponse>> getAllAccountsByUserId(@PathVariable Long userId) {
        log.debug("getAllAccountsByUserId({})", userId);
        return ResponseEntity.ok(accountService.getAllAccountsByUserId(userId));
    }
}
