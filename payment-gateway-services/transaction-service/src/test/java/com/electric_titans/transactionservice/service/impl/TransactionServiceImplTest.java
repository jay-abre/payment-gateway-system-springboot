package com.electric_titans.transactionservice.service.impl;

import com.electric_titans.transactionservice.dto.request.ScheduledTransactionRequest;
import com.electric_titans.transactionservice.dto.request.TransactionRequest;
import com.electric_titans.transactionservice.dto.response.TransactionStatusResponse;
import com.electric_titans.transactionservice.entity.ScheduledTransaction;
import com.electric_titans.transactionservice.entity.Transaction;
import com.electric_titans.transactionservice.enums.TransactionTypeEnum;
import com.electric_titans.transactionservice.repository.ScheduledTransactionRepository;
import com.electric_titans.transactionservice.repository.TransactionRepository;
import com.electric_titans.transactionservice.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ScheduledTransactionRepository scheduledTransactionRepository;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private TransactionServiceImpl transactionService;

//    @Test
//    void createTransaction() {
//        TransactionRequest transactionRequest = new TransactionRequest();
//        Transaction transaction = new Transaction();
//
//        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
//
//        Transaction result = transactionService.createTransaction(transactionRequest, request);
//
//        assertNotNull(result);
//    }

    @Test
    void scheduleTransaction() {
        ScheduledTransactionRequest scheduledTransactionRequest = new ScheduledTransactionRequest();
        ScheduledTransaction scheduledTransaction = new ScheduledTransaction();

        when(scheduledTransactionRepository.save(any(ScheduledTransaction.class))).thenReturn(scheduledTransaction);

        ScheduledTransaction result = transactionService.scheduleTransaction(scheduledTransactionRequest);

        assertNotNull(scheduledTransaction);
    }

    @Test
    void createBatchTransactions() {
        List<TransactionRequest> requestDtos = new ArrayList<>();
        requestDtos.add(new TransactionRequest());
        requestDtos.add(new TransactionRequest());

        Transaction transaction = new Transaction();
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        List<Transaction> result = transactionService.createBatchTransactions(requestDtos);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void getAllTransactions() {
        // Prepare test data
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction());
        transactions.add(new Transaction());

        Page<Transaction> page = new PageImpl<>(transactions, PageRequest.of(1, 10), transactions.size());

        PageRequest pageRequest = PageRequest.of(1, 10);
        when(transactionRepository.findAll(pageRequest)).thenReturn(page);

        Page<Transaction> result = transactionService.getAllTransactions(1, 10);

        assertEquals(2, result.getContent().size());
    }

    @Test
    void getAllScheduledTransactions() {
        List<ScheduledTransaction> scheduledTransactions = new ArrayList<>();

        scheduledTransactions.add(new ScheduledTransaction());
        scheduledTransactions.add(new ScheduledTransaction());

        when(scheduledTransactionRepository.findAll()).thenReturn(scheduledTransactions);

        List<ScheduledTransaction> result = transactionService.getAllScheduledTransactions();

        assertEquals(result.size(), 2);
    }

    @Test
    void getTransaction() {
        Transaction transaction = new Transaction();

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

        Transaction result = transactionService.getTransaction(1L);

        assertNotNull(result);
    }

    @Test
    void getTransactionsByType() {
        Transaction transaction = new Transaction();
        Transaction transaction2 = new Transaction();

        transaction.setTransactionType(TransactionTypeEnum.DEPOSIT);
        transaction2.setTransactionType(TransactionTypeEnum.DEPOSIT);

        List<Transaction> transactionList = new ArrayList<>();

        transactionList.add(transaction);
        transactionList.add(transaction2);

        when(transactionRepository.findByTransactionType(TransactionTypeEnum.DEPOSIT)).thenReturn(transactionList);

        List<Transaction> result = transactionService.getTransactionsByType(TransactionTypeEnum.DEPOSIT);

        assertNotNull(result);
        assertEquals(result.size(), 2);
    }

//    @Test
//    void cancelTransaction() {
//    }

    @Test
    void getTransactionStatus() {
        Transaction transaction = new Transaction();

        when(transactionRepository.findById(any(Long.class))).thenReturn(Optional.of(transaction));

        TransactionStatusResponse result = transactionService.getTransactionStatus(1L);

        assertNotNull(result);
    }
}