package com.electric_titans.bankreconciliationservice.controller;

import com.electric_titans.bankreconciliationservice.dto.*;
import com.electric_titans.bankreconciliationservice.service.Impl.ReconciliationServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/reconciliations")
public class ReconciliationController {

    @Autowired
    private ReconciliationServiceImpl reconciliationService;

    @Operation(
            summary = "create temporary entries in database",
            description = "temporary post mapping"
    )
    @PostMapping("/tempcreate")
    public ResponseEntity<String> putTemp(){
        log.debug("putTemp()");
        reconciliationService.createTempReconciliations();

        return ResponseEntity.ok("got through method");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(
            summary = "Retrieve transaction history",
            description = "Fetches a history of transactions between the specified start and end dates with an optional limit."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction history retrieved successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid date format or parameters.")
    })
    @GetMapping("/transactions")
    public ResponseEntity<TransactionHistoryResponse> getTransactionHistory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
            @RequestParam(defaultValue = "100") Integer limit) {

        log.debug("getTransactionHistory({},{},{})", startDate, endDate, limit);
        return ResponseEntity.ok(reconciliationService.getTransactionHistory(startDate, endDate, limit));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(
            summary = "Retrieve a specific reconciliation by ID",
            description = "Fetches reconciliation details for a specific reconciliation ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reconciliation retrieved successfully.")
    })
    @GetMapping("/{reconciliationId}")
    public ResponseEntity<ReconciliationResponse> getReconciliation(
            @PathVariable Long reconciliationId) {

        log.debug("getReconciliation({})", reconciliationId);
        return ResponseEntity.ok(reconciliationService.getReconciliationById(reconciliationId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(
            summary = "Generate reconciliation report",
            description = "Creates a reconciliation report for transactions between the specified start and end dates."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reconciliation report generated successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid date format.")
    })
    @GetMapping("/report")
    public ResponseEntity<ReportResponse> getReconciliationReport(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate) {

        log.debug("getReconciliationReport({},{})", startDate, endDate);
        return ResponseEntity.ok(reconciliationService.createReconciliationReport(startDate, endDate));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(
            summary = "Download reconciliation report",
            description = "Creates a reconciliation report for transactions between the specified start and end dates and downloads it."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reconciliation report downloaded successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid date format.")
    })
    @GetMapping("/report/download")
    public ResponseEntity<String> downloadReconciliationReport(
            HttpServletResponse response,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate){
        log.debug("downloadReconciliationReport({},{})", startDate, endDate);
        reconciliationService.loadToCsv(response, startDate, endDate);

        return ResponseEntity.ok("success");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(
            summary = "Retrieve all reconciliations",
            description = "Fetches a list of all reconciliations."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of all reconciliations retrieved successfully.")
    })
    @GetMapping("/all")
    public ResponseEntity<List<ReconciliationResponse>> getAllReconciliations() {

        log.debug("getAllReconciliations()");
        return ResponseEntity.ok(reconciliationService.getAllReconciliations());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @GetMapping("/unmatched")
    public List<ReconciliationResponse> getUnmatchedReconciliations(){
        log.debug("getUnmatchedReconciliations()");
        return reconciliationService.getUnmatchedReconciliations();
    }

}