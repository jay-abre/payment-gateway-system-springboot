package com.electric_titans.transactionservice.controller;

import com.electric_titans.transactionservice.dto.request.ScheduledTransactionRequest;
import com.electric_titans.transactionservice.dto.request.TransactionRequest;
import com.electric_titans.transactionservice.dto.response.TransactionResponse;
import com.electric_titans.transactionservice.dto.response.TransactionStatusResponse;
import com.electric_titans.transactionservice.entity.ScheduledTransaction;
import com.electric_titans.transactionservice.entity.Transaction;
import com.electric_titans.transactionservice.enums.TransactionTypeEnum;
import com.electric_titans.transactionservice.mapper.TransactionMapper;
import com.electric_titans.transactionservice.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionServiceController {

    private final TransactionService transactionService;

    @Operation(
            summary = "Create a new transaction",
            description = "Creates a new transaction and saves it to the database."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transaction created successfully."),
    })
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(
            @RequestBody TransactionRequest transaction, HttpServletRequest request) {
        Transaction createdTransaction = transactionService.createTransaction(transaction, request);
        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Create a batch of transactions",
            description = "Creates multiple transactions in a batch and saves them to the database."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transactions created successfully."),
    })
    @PostMapping("/batch")
    public ResponseEntity<List<Transaction>> createBatchTransactions(
            @RequestBody List<TransactionRequest> transactions) {
        List<Transaction> createdTransactions = transactionService.createBatchTransactions(transactions);
        return new ResponseEntity<>(createdTransactions, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Schedule a transaction",
            description = "Schedules a transaction to be processed at a later time."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transaction scheduled successfully."),
    })
    @PostMapping("/schedule")
    public ResponseEntity<ScheduledTransaction> scheduleTransaction(
            @RequestBody ScheduledTransactionRequest transaction) {
        ScheduledTransaction scheduledTransaction = transactionService.scheduleTransaction(transaction);
        return new ResponseEntity<>(scheduledTransaction, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Retrieve a transaction by ID",
            description = "Fetches the details of a transaction by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction retrieved successfully."),
    })
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable Long id) {
        Transaction transaction = transactionService.getTransaction(id);
        return ResponseEntity.ok(transaction);
    }

    @Operation(
            summary = "Cancel a transaction",
            description = "Cancels a transaction by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Transaction canceled successfully."),
    })
    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelTransaction(@PathVariable Long id) {
        transactionService.cancelTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Retrieve all transactions",
            description = "Fetches a list of all transactions."
    )
    @ApiResponse(responseCode = "200", description = "List of all transactions retrieved successfully.")
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions(@RequestParam int pageNo, @RequestParam int pageSize) {
        Page<Transaction> transactions = transactionService.getAllTransactions(pageNo, pageSize);
        List<TransactionResponse> transactionResponses = new ArrayList<>();
        for (Transaction transaction : transactions) {
            transactionResponses.add(TransactionMapper.INSTANCE.toTransactionResponse(transaction));
        }
        return ResponseEntity.ok(transactionResponses);
    }

    @Operation(
            summary = "Retrieve all scheduled transactions",
            description = "Fetches a list of all scheduled transactions."
    )
    @ApiResponse(responseCode = "200", description = "List of all scheduled transactions retrieved successfully.")
    @GetMapping("/scheduled")
    public ResponseEntity<List<ScheduledTransaction>> getAllScheduledTransactions() {
        List<ScheduledTransaction> scheduledTransactions = transactionService.getAllScheduledTransactions();
        return ResponseEntity.ok(scheduledTransactions);
    }

    @Operation(
            summary = "Retrieve transaction status by ID",
            description = "Fetches the status of a transaction by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction status retrieved successfully."),
    })
    @GetMapping("/{id}/status")
    public ResponseEntity<TransactionStatusResponse> getTransactionStatus(@PathVariable Long id) {
        TransactionStatusResponse statusDto = transactionService.getTransactionStatus(id);
        return ResponseEntity.ok(statusDto);
    }

    @Operation(
            summary = "Retrieve transactions by category",
            description = "Fetches a list of transactions filtered by category."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of transactions by category retrieved successfully."),
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Transaction>> getTransactionsByCategory(@PathVariable TransactionTypeEnum category) {
        List<Transaction> transactions = transactionService.getTransactionsByType(category);
        return ResponseEntity.ok(transactions);
    }
}