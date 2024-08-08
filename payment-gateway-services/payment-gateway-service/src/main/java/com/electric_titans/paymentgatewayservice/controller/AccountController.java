package com.electric_titans.paymentgatewayservice.controller;

import com.electric_titans.common.event.AccountCreatedEvent;
import com.electric_titans.paymentgatewayservice.service.AccountService;
import com.stripe.exception.StripeException;
import com.stripe.model.Account;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stripe")
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountService accountService;

    @Operation(
            summary = "Create Account",
            description = "Creates a new account with the provided email and country."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input")
    })
    @PostMapping("/create-account")
    public ResponseEntity<?> createAccount(@RequestBody AccountCreatedEvent event) {
        logger.info("Creating account for email: {}", event.getEmail());
        accountService.createAccount(event.getEmail(), event.getCountry());
        logger.info("Account created successfully for email: {}", event.getEmail());
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Update Account",
            description = "Updates an existing account with the provided email and new country information."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PutMapping("/update-account/{email}")
    public ResponseEntity<?> updateAccount(@PathVariable String email, @RequestBody AccountCreatedEvent event) {
        logger.info("Updating account for email: {}", email);
        accountService.updateAccount(email, event.getCountry());
        logger.info("Account updated successfully for email: {}", email);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Delete Account",
            description = "Deletes an account identified by the provided email."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @DeleteMapping("/delete-account/{email}")
    public ResponseEntity<?> deleteAccount(@PathVariable String email) {
        logger.info("Deleting account for email: {}", email);
        accountService.deleteAccount(email);
        logger.info("Account deleted successfully for email: {}", email);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Get Account by ID",
            description = "Retrieves an account by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved account"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/get-account/{accountId}")
    public ResponseEntity<?> getAccount(@PathVariable String accountId) {
        logger.info("Retrieving account for ID: {}", accountId);
        Account account = accountService.getAccount(accountId);
        logger.info("Account retrieved successfully for ID: {}", accountId);
        return ResponseEntity.ok(account);
    }

    @Operation(
            summary = "List All Accounts",
            description = "Lists all accounts in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully listed all accounts"),
            @ApiResponse(responseCode = "500", description = "Failed to list accounts")
    })
    @GetMapping("/list-all-accounts")
    public ResponseEntity<?> listAllAccounts() {
        logger.info("Listing all accounts");
        try {
            List<Account> accounts = accountService.listAllAccounts();
            logger.info("All accounts listed successfully");
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            logger.error("Failed to list accounts", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to list accounts: " + e.getMessage());
        }
    }
}