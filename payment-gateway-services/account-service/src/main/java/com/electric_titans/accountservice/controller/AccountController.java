package com.electric_titans.accountservice.controller;

import com.electric_titans.accountservice.dto.request.AccountRequest;
import com.electric_titans.accountservice.dto.request.AccountStatusUpdateRequest;
import com.electric_titans.accountservice.dto.response.AccountResponse;
import com.electric_titans.accountservice.dto.response.AccountStatusUpdateResponse;
import com.electric_titans.accountservice.dto.response.BalanceResponse;
import com.electric_titans.accountservice.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
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
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    @Operation(
            summary = "Create a new account",
            description = "Creates a new account and returns the details of the created account."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account created successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid input data.")
    })
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@RequestBody @Valid AccountRequest accountRequest, HttpServletRequest request) {
        return new ResponseEntity<>(accountService.createAccount(accountRequest, request), HttpStatus.CREATED);
    }

    @Operation(
            summary = "Retrieve an account by ID",
            description = "Fetches the details of an account by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account retrieved successfully."),
    })
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccountByAccountId(
            @PathVariable Long accountId) {
        return ResponseEntity.ok(accountService.getAccountByAccountId(accountId));
    }

    @Operation(
            summary = "Update an account by ID",
            description = "Updates the details of an existing account by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account updated successfully."),
    })
    @PutMapping("/{accountId}")
    public ResponseEntity<AccountResponse> updateAccountByAccountId(@PathVariable Long accountId,
                                                                    @RequestBody @Valid AccountRequest accountRequest) {
        return ResponseEntity.ok(accountService.updateAccountByAccountId(accountId, accountRequest));
    }

    @Operation(
            summary = "Deactivate an account by ID",
            description = "Deactivates an account by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Account deactivated successfully."),
    })
    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deactivateAccountByAccountId(
            @PathVariable Long accountId) {
        accountService.deactivateAccountByAccountId(accountId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get current balance by account ID",
            description = "Fetches the current balance of an account by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Current balance retrieved successfully."),
    })
    @GetMapping("/{accountId}/balance")
    public ResponseEntity<BalanceResponse> getCurrentBalanceByAccountId(
            @PathVariable Long accountId) {
        return ResponseEntity.ok(accountService.getCurrentBalanceByAccountId(accountId));
    }

    @Operation(
            summary = "Update account status by ID",
            description = "Updates the status of an account by its ID. Requires admin privileges."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account status updated successfully."),
    })
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @PutMapping("/{accountId}/status")
    public ResponseEntity<AccountStatusUpdateResponse> updateAccountStatusByAccountId(@PathVariable Long accountId,
                                                                                      @RequestBody @Valid AccountStatusUpdateRequest request) {
        return ResponseEntity.ok(accountService.updateAccountStatusByAccountId(accountId, request));
    }
}

