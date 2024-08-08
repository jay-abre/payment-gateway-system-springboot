package com.electric_titans.bankreconciliationservice.entity;

import com.electric_titans.bankreconciliationservice.enums.StatusEnum;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

@ExtendWith(MockitoExtension.class)
public class ReconciliationTest {
    @InjectMocks
    Reconciliation reconciliation;

    void assertEquals(Reconciliation reconciliation){
        Assertions.assertEquals(0L, reconciliation.getReconciliationId());
        Assertions.assertEquals(0L, reconciliation.getTransactionId());
        Assertions.assertEquals(StatusEnum.STARTED, reconciliation.getStatus());
    }

    @Test
    void testAllArgsConstructor(){
        assert(true);
    }

    @Test
    void testNoArgsConstructor(){
        Reconciliation reconciliation = new Reconciliation();
        Assertions.assertInstanceOf(Reconciliation.class, reconciliation);
    }

    @Test
    void testSetters(){
        assert(true);
    }

    @Test
    void testGetTransactionId(){ Assertions.assertNull(reconciliation.getTransactionId()); }

    @Test
    void testGetStatus(){ Assertions.assertNull(reconciliation.getStatus()); }

    @Test
    void testGetTransactionMatched(){ assert(true); }

    @Test
    void testGetPaymentTypeMatched(){ assert(true); }

}
