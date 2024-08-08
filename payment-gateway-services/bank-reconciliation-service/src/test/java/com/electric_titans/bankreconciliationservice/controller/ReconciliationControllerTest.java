package com.electric_titans.bankreconciliationservice.controller;

import com.electric_titans.bankreconciliationservice.dto.ReportResponse;
import com.electric_titans.bankreconciliationservice.dto.ReconciliationResponse;
import com.electric_titans.bankreconciliationservice.entity.Reconciliation;
import com.electric_titans.bankreconciliationservice.enums.StatusEnum;
import com.electric_titans.bankreconciliationservice.enums.DiscrepancyTypeEnum;
import com.electric_titans.bankreconciliationservice.repository.UserRepository;
import com.electric_titans.bankreconciliationservice.security.JwtAuthenticationFilter;
import com.electric_titans.bankreconciliationservice.service.JwtService;
import com.electric_titans.bankreconciliationservice.service.TokenBlacklistService;
import com.electric_titans.bankreconciliationservice.service.Impl.ReconciliationServiceImpl;
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(ReconciliationController.class)
public class ReconciliationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private ReconciliationServiceImpl reconciliationService;

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
    void setUp(){
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    void testGetById() throws Exception{
        ReconciliationResponse reconciliation = new ReconciliationResponse();
        reconciliation.setReconciliationId(1L);
        when(reconciliationService.getReconciliationById(anyLong())).thenReturn(reconciliation);

        mockMvc.perform(get("/api/reconciliations/{reconciliationId}", 1L)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.reconciliationId").value(reconciliation.getReconciliationId()));
    }

    @Test
    void testGetAll() throws Exception{
        ReconciliationResponse reconciliation = new ReconciliationResponse();
        reconciliation.setReconciliationId(1L);
        ReconciliationResponse reconciliation2 = new ReconciliationResponse();
        reconciliation2.setReconciliationId(2L);

        List<ReconciliationResponse> reconciliations = new ArrayList<>();
        reconciliations.add(reconciliation);
        reconciliations.add(reconciliation2);

        when(reconciliationService.getAllReconciliations()).thenReturn(reconciliations);

        mockMvc.perform(get("/api/reconciliations/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testGetUnmatched() throws Exception{
        ReconciliationResponse reconciliation = new ReconciliationResponse();
        reconciliation.setReconciliationId(1L);
        reconciliation.setStatus(StatusEnum.RECONCILED);
        ReconciliationResponse reconciliation2 = new ReconciliationResponse();
        reconciliation2.setReconciliationId(2L);
        reconciliation2.setStatus(StatusEnum.FINISHED_WITH_DISCREPANCY);

        List<ReconciliationResponse> unmatched = new ArrayList<>();
        unmatched.add(reconciliation2);

        when(reconciliationService.getUnmatchedReconciliations()).thenReturn(unmatched);

        mockMvc.perform(get("/api/reconciliations/unmatched")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
