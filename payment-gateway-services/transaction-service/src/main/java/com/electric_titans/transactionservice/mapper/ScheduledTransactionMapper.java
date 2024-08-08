package com.electric_titans.transactionservice.mapper;

import com.electric_titans.transactionservice.dto.request.ScheduledTransactionRequest;
import com.electric_titans.transactionservice.entity.ScheduledTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ScheduledTransactionMapper {

    ScheduledTransactionMapper INSTANCE = Mappers.getMapper(ScheduledTransactionMapper.class);

    ScheduledTransaction toScheduledTransaction(ScheduledTransactionRequest scheduledTransactionRequest);
}