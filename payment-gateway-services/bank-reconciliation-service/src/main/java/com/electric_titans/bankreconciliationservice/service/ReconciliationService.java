package com.electric_titans.bankreconciliationservice.service;

import com.electric_titans.bankreconciliationservice.dto.*;
import com.electric_titans.common.event.PaymentSuccessEvent;
import com.stripe.model.PaymentIntent;
import com.stripe.model.BalanceTransaction;
import jakarta.servlet.http.HttpServletResponse;

import java.time.Instant;
import java.util.Map;
import java.util.List;

public interface ReconciliationService {

    void createTempReconciliations();

    // get deposit transaction details through kafka
    void startDepositReconciliation(PaymentSuccessEvent paymentSuccessEvent);

    // get withdrawal transaction details through kafka
    void startWithdrawReconciliation(PaymentSuccessEvent paymentSuccessEvent);

    // get transfer transaction details through kafka
    void startTransferReconciliation(PaymentSuccessEvent paymentSuccessEvent);

    // get payment intent details from stripe webhook
    void handlePaymentIntentSucceeded(PaymentIntent paymentIntent);

    // handle payment intent failed case
    void handlePaymentIntentFailed(PaymentIntent paymentIntent);

    // process real time reconciliation
    void handleReconciliation(Long reconciliationId);

    // create report given start and end date
    ReportResponse createReconciliationReport(Instant start, Instant end);

    // download report as csv
    void loadToCsv(HttpServletResponse response, Instant startDate, Instant endDate);

    // get transaction history from stripe
    TransactionHistoryResponse getTransactionHistory(Instant startDate, Instant endDate, Integer limit);

    // show all reconciliations
    List<ReconciliationResponse> getAllReconciliations();

    // get reconciliation by id
    ReconciliationResponse getReconciliationById(Long reconciliationId);

    // show all unmatched reconciliations
    List<ReconciliationResponse> getUnmatchedReconciliations();

    // create date range map for transaction history
    Map<String, Long> createDateRangeMap(Instant startDate, Instant endDate);

    // convert to stripe transaction dto for transaction history
    StripeTransaction convertToDTO(BalanceTransaction transaction);

}
