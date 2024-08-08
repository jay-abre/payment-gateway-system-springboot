package com.electric_titans.transactionservice.service.impl;

import com.electric_titans.common.event.PaymentSuccessEvent;
import com.electric_titans.common.event.TransactionCreatedEvent;
import com.electric_titans.transactionservice.dto.request.ScheduledTransactionRequest;
import com.electric_titans.transactionservice.dto.request.TransactionRequest;
import com.electric_titans.transactionservice.dto.response.BalanceResponse;
import com.electric_titans.transactionservice.dto.response.TransactionStatusResponse;
import com.electric_titans.transactionservice.entity.ScheduledTransaction;
import com.electric_titans.transactionservice.entity.Transaction;
import com.electric_titans.transactionservice.entity.User;
import com.electric_titans.transactionservice.enums.StatusEnum;
import com.electric_titans.transactionservice.enums.TransactionTypeEnum;
import com.electric_titans.transactionservice.exception.InsufficientFundsException;
import com.electric_titans.transactionservice.mapper.ScheduledTransactionMapper;
import com.electric_titans.transactionservice.mapper.TransactionMapper;
import com.electric_titans.transactionservice.repository.ScheduledTransactionRepository;
import com.electric_titans.transactionservice.repository.TransactionRepository;
import com.electric_titans.transactionservice.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final ScheduledTransactionRepository scheduledTransactionRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final WebClient webClient;


    @Override
    @Transactional
    public Transaction createTransaction(TransactionRequest transactionRequest, HttpServletRequest request) {
        Transaction transaction = TransactionMapper.INSTANCE.toTransaction(transactionRequest);

        transaction.setStatus(StatusEnum.PENDING);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();

        log.info(user.getCustomerId());
        Transaction savedTransaction = transactionRepository.save(transaction);

        log.info("To account id : {}", savedTransaction.getToAccountId());
        TransactionCreatedEvent event = TransactionCreatedEvent.builder()
                .transactionId(savedTransaction.getTransactionId())
                .fromAccountId(savedTransaction.getFromAccountId())
                .toAccountId(savedTransaction.getToAccountId())
                .amount(savedTransaction.getAmount())
                .currency(savedTransaction.getCurrency())
                .customerId(user.getCustomerId())
                .amount(transaction.getAmount())
                .paymentMethod("pm_card_visa")
                .build();

        log.info("Event to account id : {}", event.getToAccountId());

        String authorization = request.getHeader("Authorization");
        String jwt = authorization.substring(7);
        BalanceResponse balanceResponse = webClient.get()
                .uri(String.format("http://localhost:8085/api/v1/accounts/%s/balance", savedTransaction.getFromAccountId()))
                .header("Authorization", String.format("Bearer %s", jwt))
                .retrieve()
                .toEntity(BalanceResponse.class)
                .block().getBody();

        Long currentBalance = balanceResponse.getBalance();

        switch (transaction.getTransactionType().name()) {
            case "DEPOSIT":
                kafkaTemplate.send("DEPOSIT-REQUEST", event);
                break;
            case "WITHDRAWAL":
                if (currentBalance.compareTo(savedTransaction.getAmount()) < 0) {
                    throw new InsufficientFundsException(currentBalance, savedTransaction.getAmount());
                }

                kafkaTemplate.send("WITHDRAW-REQUEST", event);
                break;
            case "PEER_TO_PEER":
                if (currentBalance.compareTo(savedTransaction.getAmount()) < 0) {
                    throw new InsufficientFundsException(currentBalance, savedTransaction.getAmount());
                }

                kafkaTemplate.send("TRANSFER-REQUEST", event);
                break;
        }

        return savedTransaction;
    }

    @Override
    public ScheduledTransaction scheduleTransaction(ScheduledTransactionRequest transaction) {
        ScheduledTransaction scheduledTransaction = ScheduledTransactionMapper.INSTANCE.toScheduledTransaction(transaction);

        scheduledTransaction.setStatus(StatusEnum.PENDING);

        return scheduledTransactionRepository.save(scheduledTransaction);
    }

    @Override
    public List<Transaction> createBatchTransactions(List<TransactionRequest> transactions) {
        List<Transaction> listTransactions = new ArrayList<>();

        transactions.forEach((t) -> {
            Transaction transaction = TransactionMapper.INSTANCE.toTransaction(t);
            transaction.setStatus(StatusEnum.PENDING);
            listTransactions.add(transactionRepository.save(transaction));
        });

        return listTransactions;
    }

    @Override
    public Page<Transaction> getAllTransactions(int pageNo, int pageSize) {
        System.out.println("Get all transactions: ");
        return transactionRepository.findAll(PageRequest.of(pageNo, pageSize));
    }

    @Override
    public List<ScheduledTransaction> getAllScheduledTransactions() {
        return scheduledTransactionRepository.findAll();
    }

    @Override
    public Transaction getTransaction(Long id) {
        return transactionRepository.findById(id).get();
    }

    @Override
    public List<Transaction> getTransactionsByType(TransactionTypeEnum type) {
        return transactionRepository.findByTransactionType(type);
    }


    @Override
    public void cancelTransaction(Long id) {
        transactionRepository.deleteById(id);
    }

    @Override
    public TransactionStatusResponse getTransactionStatus(Long id) {
        TransactionStatusResponse transactionStatus = TransactionMapper.INSTANCE.toTransactionStatusDto(transactionRepository.findById(id).get());
        return transactionStatus;
    }

    @Override
    @KafkaListener(topics = "DEPOSIT-SUCCESS", groupId = "transaction-service")
    public void depositSuccess(PaymentSuccessEvent paymentSuccessEvent) {
        Transaction transaction = transactionRepository.findById(paymentSuccessEvent.getTransactionId()).get();
        transaction.setStatus(StatusEnum.COMPLETED);
        transactionRepository.save(transaction);
    }

    //
//    public void publishTransactionCreatedEvent(Transaction transaction) {
//        TransactionCreatedEvent event = new TransactionCreatedEvent(transaction.getId(), transaction.getFromAccountId(), transaction.getToAccountId(), transaction.getAmount());
//        kafkaTemplate.send("TransactionCreated", event);
//    }
}
