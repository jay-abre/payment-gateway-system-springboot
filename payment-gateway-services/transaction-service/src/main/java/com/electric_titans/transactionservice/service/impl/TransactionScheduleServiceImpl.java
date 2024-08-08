package com.electric_titans.transactionservice.service.impl;

import com.electric_titans.transactionservice.entity.ScheduledTransaction;
import com.electric_titans.transactionservice.entity.Transaction;
import com.electric_titans.transactionservice.mapper.TransactionMapper;
import com.electric_titans.transactionservice.repository.ScheduledTransactionRepository;
import com.electric_titans.transactionservice.repository.TransactionRepository;
import com.electric_titans.transactionservice.service.TransactionScheduleService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.LifecycleState;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionScheduleServiceImpl implements TransactionScheduleService {
    private final ScheduledTransactionRepository scheduleRepo;
    private final TransactionRepository repo;

    @Override
    @Scheduled(cron = "0 0 * * * *")
    public void addScheduledTask() {
        List<ScheduledTransaction> scheduledTransactions = scheduleRepo.findAll();

        for(ScheduledTransaction transaction : scheduledTransactions) {
            System.out.println(transaction.getScheduledFor());
            Instant now = Instant.now();
            Instant temp = transaction.getScheduledFor();

            LocalDateTime nowTime = LocalDateTime.ofInstant(now, ZoneId.systemDefault());
            LocalDateTime tempTime = LocalDateTime.ofInstant(temp, ZoneId.systemDefault());

            System.out.println("CheckingTime");
            System.out.println(nowTime);
            System.out.println(tempTime);

            if(tempTime.isBefore(nowTime)) {
                Transaction newTransaction = TransactionMapper.INSTANCE.toTransactionFromScheduledTransaction(transaction);
                repo.save(newTransaction);
                scheduleRepo.delete(transaction);
                System.out.println("Added;");
            }
            else {
                System.out.println("Broke");
                break;
            }

        }
    }
}
