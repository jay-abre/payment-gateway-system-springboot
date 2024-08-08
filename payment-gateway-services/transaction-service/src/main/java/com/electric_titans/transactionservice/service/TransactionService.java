package com.electric_titans.transactionservice.service;

import com.electric_titans.common.event.PaymentSuccessEvent;
import com.electric_titans.transactionservice.dto.request.ScheduledTransactionRequest;
import com.electric_titans.transactionservice.dto.request.TransactionRequest;
import com.electric_titans.transactionservice.dto.response.TransactionStatusResponse;
import com.electric_titans.transactionservice.entity.ScheduledTransaction;
import com.electric_titans.transactionservice.entity.Transaction;
import com.electric_titans.transactionservice.enums.TransactionTypeEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TransactionService {
    Transaction createTransaction(TransactionRequest transaction, HttpServletRequest request);

    ScheduledTransaction scheduleTransaction(ScheduledTransactionRequest transaction);

    Page<Transaction> getAllTransactions(int pageNo, int pageSize);

    List<ScheduledTransaction> getAllScheduledTransactions();

    Transaction getTransaction(Long id);

    List<Transaction> getTransactionsByType(TransactionTypeEnum type);

    List<Transaction> createBatchTransactions(List<TransactionRequest> transactions);

    void cancelTransaction(Long id);

    TransactionStatusResponse getTransactionStatus(Long id);

    void depositSuccess(PaymentSuccessEvent paymentSuccessEvent);

//    public void publishTransactionCreatedEvent(Transaction transaction) {
//        TransactionCreatedEvent event = new TransactionCreatedEvent(transaction.getId(), transaction.getFromAccountId(), transaction.getToAccountId(), transaction.getAmount());
//        kafkaTemplate.send("TransactionCreated", event);
//    }
}
