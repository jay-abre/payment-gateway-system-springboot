package com.electric_titans.transactionservice.mapper;

import com.electric_titans.transactionservice.dto.request.TransactionRequest;
import com.electric_titans.transactionservice.dto.response.TransactionResponse;
import com.electric_titans.transactionservice.dto.response.TransactionStatusResponse;
import com.electric_titans.transactionservice.entity.ScheduledTransaction;
import com.electric_titans.transactionservice.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TransactionMapper {
    
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    TransactionResponse toTransactionResponse(Transaction transaction);
    Transaction toTransaction(TransactionRequest transactionRequest);
    @Mapping(source = "updatedAt", target = "lastUpdated")
    TransactionStatusResponse toTransactionStatusDto(Transaction transaction);

    @Mapping(target = "transactionId", ignore = true)
    Transaction toTransactionFromScheduledTransaction(ScheduledTransaction scheduledTransaction);
}
