package com.electric_titans.transactionservice.repository;

import com.electric_titans.transactionservice.entity.ScheduledTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduledTransactionRepository extends JpaRepository <ScheduledTransaction, Long> {
}
