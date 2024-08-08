package com.electric_titans.bankreconciliationservice.mapper;

import com.electric_titans.bankreconciliationservice.dto.ReconciliationResponse;
import com.electric_titans.bankreconciliationservice.entity.Reconciliation;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReconciliationMapper {

    ReconciliationMapper INSTANCE = Mappers.getMapper(ReconciliationMapper.class);

    Reconciliation reconciliationResponseToReconciliation(ReconciliationResponse reconciliationResponse);

    ReconciliationResponse reconciliationToReconciliationResponse(Reconciliation reconciliation);

}
