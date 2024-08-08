package com.electric_titans.transactionservice.controller;

import com.electric_titans.transactionservice.dto.request.ScheduledTransactionRequest;
import com.electric_titans.transactionservice.dto.request.TransactionRequest;
import com.electric_titans.transactionservice.dto.response.TransactionStatusResponse;
import com.electric_titans.transactionservice.entity.ScheduledTransaction;
import com.electric_titans.transactionservice.entity.Transaction;
import com.electric_titans.transactionservice.enums.StatusEnum;
import com.electric_titans.transactionservice.enums.TransactionTypeEnum;
import com.electric_titans.transactionservice.repository.UserRepository;
import com.electric_titans.transactionservice.security.JwtAuthenticationFilter;
import com.electric_titans.transactionservice.service.JwtService;
import com.electric_titans.transactionservice.service.TokenBlacklistService;
import com.electric_titans.transactionservice.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(TransactionServiceController.class)
public class TransactionServiceControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private HttpServletRequest request;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();;
    }

//    @Test
//    void createTransaction_shouldReturnCreatedTransaction() throws Exception{
//        TransactionRequest requestDto = new TransactionRequest();
//        Transaction transaction = new Transaction();
//        when(transactionService.createTransaction(any(TransactionRequest.class), request)).thenReturn(transaction);
//
//        // Act & Assert
//        mockMvc.perform(post("/api/v1/transactions")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(requestDto)))
//                .andExpect(MockMvcResultMatchers.status().isCreated())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.transactionId").value(transaction.getTransactionId()));
//    }

    @Test
    void createBatchTransactions_shouldReturnCreatedTransactions() throws Exception {
        Transaction transOne = new Transaction();
        Transaction transTwo = new Transaction();

        TransactionRequest transReqOne = new TransactionRequest();
        TransactionRequest transReqTwo = new TransactionRequest();

        transOne.setTransactionId(1L);
        transTwo.setTransactionId(2L);

        List<Transaction> transactions = Arrays.asList(transOne, transTwo);
        List<TransactionRequest> transactionRequestRespons = Arrays.asList(transReqOne, transReqTwo);
        when(transactionService.createBatchTransactions(any())).thenReturn(transactions);

        mockMvc.perform(post("/api/v1/transactions/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(transactionRequestRespons))) // Replace with actual JSON structure
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void scheduleTransaction_shouldReturnScheduledTransaction() throws Exception {
        ScheduledTransactionRequest requestDto = new ScheduledTransactionRequest();
        ScheduledTransaction scheduledTransaction = new ScheduledTransaction();
        when(transactionService.scheduleTransaction(any(ScheduledTransactionRequest.class))).thenReturn(scheduledTransaction);

        mockMvc.perform(post("/api/v1/transactions/schedule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto))) // Replace with actual JSON structure
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.transactionId").value(scheduledTransaction.getTransactionId()));
    }

    @Test
    void getTransaction_shouldReturnTransaction() throws Exception {
        Long id = 1L;
        Transaction transaction = new Transaction();
        transaction.setTransactionId(id);
        when(transactionService.getTransaction(anyLong())).thenReturn(transaction);

        mockMvc.perform(get("/api/v1/transactions/{id}", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.transactionId").value(transaction.getTransactionId()));
    }

    @Test
    void cancelTransaction_shouldCallService() throws Exception {
        // Arrange
        Long id = 1L;

        // Act & Assert
        mockMvc.perform(delete("/api/v1/transactions/{id}/cancel", id))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(transactionService).cancelTransaction(id);
    }

    @Test
    void getAllTransactions_shouldReturnTransactions() throws Exception {
        Page<Transaction> transactions = new PageImpl<>(Arrays.asList(new Transaction(), new Transaction()));
        when(transactionService.getAllTransactions(any(int.class), any(int.class))).thenReturn(transactions);

        mockMvc.perform(get("/api/v1/transactions")
                        .param("pageNo", "0") // Add pageNo parameter
                        .param("pageSize", "10") // Add pageSize parameter
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getAllScheduledTransactions_shouldReturnScheduledTransactions() throws Exception {

        ScheduledTransaction transOne = new ScheduledTransaction();
        ScheduledTransaction transTwo = new ScheduledTransaction();

        transOne.setTransactionId(1L);
        transTwo.setTransactionId(2L);

        List<ScheduledTransaction> scheduledTransactions = Arrays.asList(transOne, transTwo);
        when(transactionService.getAllScheduledTransactions()).thenReturn(scheduledTransactions);

        mockMvc.perform(get("/api/v1/transactions/scheduled")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].transactionId").value(scheduledTransactions.get(0).getTransactionId()));
    }

        @Test
        void getTransactionStatus_shouldReturnTransactionStatus() throws Exception {
            Long id = 1L;
            TransactionStatusResponse statusDto = new TransactionStatusResponse();
            statusDto.setStatus(StatusEnum.PENDING);

            when(transactionService.getTransactionStatus(anyLong())).thenReturn(statusDto);

            mockMvc.perform(get("/api/v1/transactions/{id}/status", id)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath("$.status").value("PENDING")); // Adjust the path as needed
        }

    @Test
    void getTransactionsByCategory_shouldReturnTransactions() throws Exception {
        TransactionTypeEnum category = TransactionTypeEnum.DEPOSIT;

        Transaction transOne = new Transaction();
        Transaction transTwo = new Transaction();

        transOne.setTransactionId(1L);
        transTwo.setTransactionId(2L);

        List<Transaction> transactions = Arrays.asList(transOne, transTwo);

        when(transactionService.getTransactionsByType(any(TransactionTypeEnum.class))).thenReturn(transactions);

        mockMvc.perform(get("/api/v1/transactions/category/{category}", category.name())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].transactionId").value(transactions.get(0).getTransactionId()));
    }
}
