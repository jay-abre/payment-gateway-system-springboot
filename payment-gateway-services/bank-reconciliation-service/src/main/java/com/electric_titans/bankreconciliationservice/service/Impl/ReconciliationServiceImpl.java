package com.electric_titans.bankreconciliationservice.service.Impl;

import com.electric_titans.bankreconciliationservice.dto.*;
import com.electric_titans.bankreconciliationservice.entity.Reconciliation;
import com.electric_titans.bankreconciliationservice.enums.DiscrepancyTypeEnum;
import com.electric_titans.bankreconciliationservice.exception.ResourceNotFoundException;
import com.electric_titans.bankreconciliationservice.mapper.ReconciliationMapper;
import com.electric_titans.bankreconciliationservice.service.ReconciliationService;
import com.electric_titans.bankreconciliationservice.repository.ReconciliationRepository;
import com.electric_titans.bankreconciliationservice.enums.StatusEnum;
import com.electric_titans.common.enums.TransactionTypeEnum;
import com.electric_titans.common.event.PaymentSuccessEvent;
import com.electric_titans.common.event.ReconciliationEvent;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.BalanceTransaction;
import com.stripe.model.BalanceTransactionCollection;
import com.stripe.model.PaymentIntent;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.time.Instant;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReconciliationServiceImpl implements ReconciliationService{

    @Autowired
    private ReconciliationRepository reconciliationRepository;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @Override
    public void createTempReconciliations(){
        Reconciliation reconciliation1 = Reconciliation.builder()
                .customerId("cus-1")
                .transactionId(100L)
                .stripeId("str-1")
                .status(StatusEnum.RECONCILED)
                .transactionDate(Instant.parse("2024-07-25T15:23:44Z"))
                .transactionAmount(1000L)
                .paymentAmount(1000L)
                .transactionCurrency("USD")
                .paymentCurrency("USD")
                .discrepancyAmount(0L)
                .discrepancyType(DiscrepancyTypeEnum.NONE)
                .completedAt(Instant.parse("2024-08-02T15:24:44Z"))
                .transactionType(TransactionTypeEnum.DEPOSIT)
                .build();

        reconciliationRepository.save(reconciliation1);

        Reconciliation reconciliation2 = Reconciliation.builder()
                .customerId("cus-1")
                .transactionId(101L)
                .stripeId("str-2")
                .status(StatusEnum.FINISHED_WITH_DISCREPANCY)
                .transactionDate(Instant.parse("2024-07-26T15:23:44Z"))
                .transactionAmount(1000L)
                .paymentAmount(500L)
                .transactionCurrency("USD")
                .paymentCurrency("USD")
                .discrepancyAmount(500L)
                .discrepancyType(DiscrepancyTypeEnum.PAYMENT_AMOUNT)
                .completedAt(Instant.parse("2024-08-02T09:00:00Z"))
                .transactionType(TransactionTypeEnum.DEPOSIT)
                .build();

        reconciliationRepository.save(reconciliation2);

        Reconciliation reconciliation3 = Reconciliation.builder()
                .customerId("cus-2")
                .transactionId(102L)
                .stripeId("str-3")
                .status(StatusEnum.FINISHED_WITH_DISCREPANCY)
                .transactionDate(Instant.parse("2024-07-27T15:23:44Z"))
                .transactionAmount(1000L)
                .paymentAmount(1000L)
                .transactionCurrency("PHP")
                .paymentCurrency("USD")
                .discrepancyAmount(0L)
                .discrepancyType(DiscrepancyTypeEnum.CURRENCY)
                .transactionType(TransactionTypeEnum.DEPOSIT)
                .completedAt(Instant.parse("2024-08-02T10:00:00Z"))
                .build();

        reconciliationRepository.save(reconciliation3);

        Reconciliation reconciliation4 = Reconciliation.builder()
                .customerId("cus-3")
                .transactionId(103L)
                .stripeId("str-4")
                .status(StatusEnum.FAILED)
                .transactionDate(Instant.parse("2024-08-02T15:23:44Z"))
                .transactionAmount(1000L)
                .transactionCurrency("USD")
                .paymentAmount(0L)
                .paymentCurrency("")
                .discrepancyAmount(1000L)
                .transactionType(TransactionTypeEnum.DEPOSIT)
                .discrepancyType(DiscrepancyTypeEnum.PAYMENT_FAILED)
                .completedAt(Instant.parse("2024-08-02T11:00:00Z"))
                .build();

        reconciliationRepository.save(reconciliation4);
    }

    @Override
    @KafkaListener(topics = "DEPOSIT-SUCCESS", groupId = "bank-reconciliation-service")
    public void startDepositReconciliation(PaymentSuccessEvent paymentSuccessEvent){
        Reconciliation reconciliation = Reconciliation.builder()
                        .status(StatusEnum.NOT_STARTED)
                        .transactionId(paymentSuccessEvent.getTransactionId())
                        .customerId(paymentSuccessEvent.getCustomerId())
                        .transactionType(TransactionTypeEnum.DEPOSIT)
                        .transactionAmount(paymentSuccessEvent.getTransactionAmount())
                        .stripeId(paymentSuccessEvent.getPaymentIntentId())
                        .transactionCurrency(paymentSuccessEvent.getCurrency())
                        .fromAccountId(paymentSuccessEvent.getFromAccountId())
                .build();

        reconciliationRepository.save(reconciliation);
    }

    @Override
    @KafkaListener(topics = "WITHDRAW-SUCCESS", groupId = "bank-reconciliation-service")
    public void startWithdrawReconciliation(PaymentSuccessEvent paymentSuccessEvent){
        Reconciliation reconciliation = Reconciliation.builder()
                .status(StatusEnum.NOT_STARTED)
                .transactionId(paymentSuccessEvent.getTransactionId())
                .customerId(paymentSuccessEvent.getCustomerId())
                .transactionType(TransactionTypeEnum.WITHDRAWAL)
                .transactionAmount(paymentSuccessEvent.getTransactionAmount())
                .stripeId(paymentSuccessEvent.getPaymentIntentId())
                .transactionCurrency(paymentSuccessEvent.getCurrency())
                .fromAccountId(paymentSuccessEvent.getFromAccountId())
                .build();

        reconciliationRepository.save(reconciliation);
    }

    @Override
    @KafkaListener(topics = "TRANSFER-SUCCESS", groupId = "bank-reconciliation-service")
    public void startTransferReconciliation(PaymentSuccessEvent paymentSuccessEvent){
        Reconciliation reconciliation = Reconciliation.builder()
                .status(StatusEnum.NOT_STARTED)
                .transactionId(paymentSuccessEvent.getTransactionId())
                .fromAccountId(paymentSuccessEvent.getFromAccountId())
                .toAccountId(paymentSuccessEvent.getToAccountId())
                .customerId(paymentSuccessEvent.getCustomerId())
                .transactionType(TransactionTypeEnum.PEER_TO_PEER)
                .transactionAmount(paymentSuccessEvent.getTransactionAmount())
                .stripeId(paymentSuccessEvent.getPaymentIntentId())
                .transactionCurrency(paymentSuccessEvent.getCurrency())
                .build();

        System.out.println(reconciliation.getStatus());

        reconciliationRepository.save(reconciliation);
    }

    @Override
    public void handlePaymentIntentSucceeded(PaymentIntent paymentIntent){
        log.info(String.valueOf(paymentIntent.getAmount()));
        log.info(paymentIntent.getCustomer());
        Reconciliation reconciliation = reconciliationRepository
                .findByStripeId(paymentIntent.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "PaymentId", paymentIntent.getId()));

        reconciliation.setPaymentAmount(paymentIntent.getAmount());
        reconciliation.setPaymentCurrency(paymentIntent.getCurrency());
        reconciliation.setTransactionDate(Instant.ofEpochSecond(paymentIntent.getCreated()));

        try{
            kafkaTemplate.send("RECONCILIATION-STARTED", reconciliation.getReconciliationId());
            reconciliation.setStatus(StatusEnum.STARTED);
        } catch(Exception e){
            e.printStackTrace();
            reconciliation.setStatus(StatusEnum.FAILED);
            reconciliation.setDiscrepancyAmount(reconciliation.getTransactionAmount());
            reconciliation.setDiscrepancyType(DiscrepancyTypeEnum.RECONCILIATION_FAILED);
            reconciliation.setCompletedAt(Instant.now());
        }

        reconciliationRepository.save(reconciliation);
    }

    @Override
    public void handlePaymentIntentFailed(PaymentIntent paymentIntent){
        Reconciliation reconciliation = reconciliationRepository
                .findByStripeId(paymentIntent.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Reconciliation", "CustomerId", paymentIntent.getCustomer()));
        reconciliation.setStripeId(paymentIntent.getId());

        reconciliation.setStatus(StatusEnum.FAILED);
        reconciliation.setDiscrepancyType(DiscrepancyTypeEnum.PAYMENT_FAILED);
        reconciliation.setDiscrepancyAmount(reconciliation.getTransactionAmount());
        reconciliation.setPaymentAmount(0L);
        reconciliation.setPaymentCurrency("");
        reconciliation.setCompletedAt(Instant.now());
        reconciliationRepository.save(reconciliation);
    }

    @Override
    @KafkaListener(topics = "RECONCILIATION-STARTED", groupId = "bank-reconciliation-service")
    public void handleReconciliation(Long reconciliationId){
        Stripe.apiKey = stripeApiKey;

        Reconciliation reconciliation = reconciliationRepository
                .findById(reconciliationId)
                .orElseThrow(()-> new ResourceNotFoundException("Reconciliation", "id", reconciliationId.toString()));

        if(!reconciliation.getTransactionCurrency().equals(reconciliation.getPaymentCurrency())){
            reconciliation.setDiscrepancyType(DiscrepancyTypeEnum.CURRENCY);
            reconciliation.setStatus(StatusEnum.FINISHED_WITH_DISCREPANCY);
        }
        else{
            reconciliation.setDiscrepancyType(DiscrepancyTypeEnum.NONE);
            reconciliation.setStatus(StatusEnum.RECONCILED);
        }

        if(!reconciliation.getTransactionAmount().equals(reconciliation.getPaymentAmount())){
            reconciliation.setDiscrepancyAmount(Math.abs(reconciliation.getTransactionAmount() - reconciliation.getPaymentAmount()));
            reconciliation.setDiscrepancyType(DiscrepancyTypeEnum.PAYMENT_AMOUNT);
            reconciliation.setStatus(StatusEnum.FINISHED_WITH_DISCREPANCY);
        }
        else{
            reconciliation.setDiscrepancyType(DiscrepancyTypeEnum.NONE);
            reconciliation.setDiscrepancyAmount(0L);
            reconciliation.setStatus(StatusEnum.RECONCILED);
        }

        reconciliation.setCompletedAt(Instant.now());
        reconciliationRepository.save(reconciliation);

        kafkaTemplate.send("RECONCILIATION-FINISHED", ReconciliationEvent.builder()
                        .customerId(reconciliation.getCustomerId())
                        .status(reconciliation.getStatus().toString())
                        .date(reconciliation.getCompletedAt())
                        .amount(reconciliation.getTransactionAmount())
                        .currency(reconciliation.getTransactionCurrency())
                .build());
    }

    @Override
    public TransactionHistoryResponse getTransactionHistory(Instant startDate, Instant endDate, Integer limit){
        Stripe.apiKey = stripeApiKey;

        Map<String, Object> params = new HashMap<>();
        params.put("limit", limit);
        params.put("created", createDateRangeMap(startDate, endDate));
        params.put("expand", List.of("data.source"));

        try{
            BalanceTransactionCollection transactions = BalanceTransaction.list(params);

            List<StripeTransaction> transactionDTOs = new ArrayList<>();
            for(BalanceTransaction transaction : transactions.getData()){
                transactionDTOs.add(convertToDTO(transaction));
            }

            TransactionHistoryResponse response = new TransactionHistoryResponse(
                    transactionDTOs,
                    transactions.getHasMore(),
                    transactions.getUrl()
            );

            return response;
        } catch(StripeException strex){
            strex.printStackTrace();
            return new TransactionHistoryResponse();
        }
    }

    @Override
    public ReportResponse createReconciliationReport(Instant start, Instant end){
        List<Reconciliation> reconciliations = reconciliationRepository
                .findAll()
                .stream()
                .filter(r -> r.getCompletedAt().isBefore(end) && r.getCompletedAt().isAfter(start))
                .toList();

        Long stripeAmount = reconciliations
                .stream()
                .mapToLong(Reconciliation::getPaymentAmount)
                .sum();
        Long transactionAmount = reconciliations
                .stream()
                .mapToLong(Reconciliation::getTransactionAmount)
                .sum();

        return ReportResponse.builder()
                .generatedAt(Instant.now())
                .startDate(start)
                .endDate(end)
                .totalTransactions(reconciliations.size())
                .matchedTransactions(reconciliations
                        .stream()
                        .filter(r -> r.getDiscrepancyType().equals(DiscrepancyTypeEnum.NONE))
                        .toList()
                        .size())
                .unmatchedTransactions(reconciliations
                        .stream()
                        .filter(r -> r.getDiscrepancyType().equals(DiscrepancyTypeEnum.CURRENCY) || r.getDiscrepancyType().equals(DiscrepancyTypeEnum.PAYMENT_AMOUNT))
                        .toList()
                        .size())
                .failedTransactions(reconciliations
                        .stream()
                        .filter(r -> r.getDiscrepancyType().equals(DiscrepancyTypeEnum.PAYMENT_FAILED) || r.getDiscrepancyType().equals(DiscrepancyTypeEnum.RECONCILIATION_FAILED))
                        .toList()
                        .size())
                .totalAmountStripe(stripeAmount)
                .totalAmountInternal(transactionAmount)
                .discrepancyAmount(Math.abs(transactionAmount - stripeAmount))
                .unmatchedReconciliations(reconciliations
                        .stream()
                        .filter(r -> r.getDiscrepancyType().equals(DiscrepancyTypeEnum.CURRENCY) || r.getDiscrepancyType().equals(DiscrepancyTypeEnum.PAYMENT_AMOUNT))
                        .map(ReconciliationMapper.INSTANCE::reconciliationToReconciliationResponse)
                        .toList())
                .failedReconciliations(reconciliations
                        .stream()
                        .filter(r -> r.getDiscrepancyType().equals(DiscrepancyTypeEnum.PAYMENT_FAILED) || r.getDiscrepancyType().equals(DiscrepancyTypeEnum.RECONCILIATION_FAILED))
                        .map(ReconciliationMapper.INSTANCE::reconciliationToReconciliationResponse)
                        .toList())
                .build();
    }

    @Override
    public void loadToCsv(HttpServletResponse response, Instant startDate, Instant endDate){
        ReportResponse reportResponse = createReconciliationReport(startDate, endDate);

        response.setContentType("text/csv; charset=utf-8");

        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment;filename=reconciliation-report_%s.csv", reportResponse.getGeneratedAt().toString().replaceAll("[:+.]", "-"));
        response.setHeader(headerKey, headerValue);

        try{
            ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
            String[] csvHeader = {"START_DATE", "END_DATE", "TOTAL_TRANSACTIONS", "MATCHED_TRANSACTIONS",
                    "UNMATCHED TRANSACTIONS", "FAILED_TRANSACTIONS", "STRIPE_TOTAL", "INTERNAL_TOTAL", "DISCREPANCY_AMOUNT",
                    " ", " ", " ", " "};
            String[] csvHeaderReconciliations = {"RECONCILIATION_ID", "CUSTOMER_ID", "TRANSACTION_TYPE", "PAYMENT_INTENT_ID", "STATUS",
                    "TRANSACTION_DATE", "TRANSACTION_AMOUNT", "PAYMENT_AMOUNT", "TRANSACTION_CURRENCY", "PAYMENT_CURRENCY",
                    "DISCREPANCY_AMOUNT","DISCREPANCY_TYPE","COMPLETED_AT"};
            String[] nameMapping = {"startDate", "endDate", "totalTransactions", "matchedTransactions", "unmatchedTransactions",
                    "failedTransactions", "totalAmountStripe", "totalAmountInternal", "discrepancyAmount"};
            String[] nameMappingReconciliations = {"reconciliationId", "customerId", "transactionType", "stripeId", "status",
                    "transactionDate", "transactionAmount", "paymentAmount", "transactionCurrency", "paymentCurrency",
                    "discrepancyAmount", "discrepancyType", "completedAt"};

            csvWriter.writeHeader(csvHeader);
            csvWriter.write(reportResponse, nameMapping);

            csvWriter.writeComment(" ");
            csvWriter.writeComment("RECONCILIATIONS WITH DISCREPANCY");
            csvWriter.writeHeader(csvHeaderReconciliations);

            for(ReconciliationResponse reconciliation : reportResponse.getUnmatchedReconciliations()){
                csvWriter.write(reconciliation, nameMappingReconciliations);
            }

            csvWriter.writeComment(" ");
            csvWriter.writeComment("FAILED RECONCILIATIONS");
            csvWriter.writeHeader(csvHeaderReconciliations);

            for(ReconciliationResponse reconciliation : reportResponse.getFailedReconciliations()){
                csvWriter.write(reconciliation, nameMappingReconciliations);
            }

            csvWriter.close();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public List<ReconciliationResponse> getAllReconciliations() {
        return reconciliationRepository.findAll()
                .stream()
                .map(ReconciliationMapper.INSTANCE::reconciliationToReconciliationResponse)
                .toList();
    }

    @Override
    public ReconciliationResponse getReconciliationById(Long reconciliationId){
        return reconciliationRepository.findById(reconciliationId)
                .map(ReconciliationMapper.INSTANCE::reconciliationToReconciliationResponse)
                .orElseThrow(()-> new ResourceNotFoundException("Reconciliation", "id", reconciliationId.toString()));
    }

    @Override
    public List<ReconciliationResponse> getUnmatchedReconciliations(){
        return reconciliationRepository.findAll()
                .stream()
                .filter(r -> r.getStatus().equals(StatusEnum.FINISHED_WITH_DISCREPANCY))
                .map(ReconciliationMapper.INSTANCE::reconciliationToReconciliationResponse)
                .toList();
    }

    @Override
    public Map<String, Long> createDateRangeMap(Instant startDate, Instant endDate) {
        Map<String, Long> dateRange = new HashMap<>();
        dateRange.put("gte", startDate.getEpochSecond());
        dateRange.put("lte", endDate.getEpochSecond());
        return dateRange;
    }

    @Override
    public StripeTransaction convertToDTO(BalanceTransaction transaction) {
        return new StripeTransaction(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getCreated(),
                transaction.getDescription(),
                transaction.getFee(),
                transaction.getNet(),
                transaction.getStatus(),
                transaction.getType(),
                transaction.getCurrency(),
                transaction.getSource() != null ? transaction.getSource() : null
        );
    }

}