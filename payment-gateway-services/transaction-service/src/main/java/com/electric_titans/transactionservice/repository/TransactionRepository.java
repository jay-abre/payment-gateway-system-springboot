package com.electric_titans.transactionservice.repository;

import com.electric_titans.transactionservice.entity.Transaction;
import com.electric_titans.transactionservice.enums.TransactionTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository <Transaction, Long> {
    List<Transaction> findByTransactionType(TransactionTypeEnum transactionType);
}
