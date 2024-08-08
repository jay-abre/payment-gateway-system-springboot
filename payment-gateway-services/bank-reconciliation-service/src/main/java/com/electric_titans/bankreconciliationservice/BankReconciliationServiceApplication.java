package com.electric_titans.bankreconciliationservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Bank Reconciliation Service REST API Documentation",
				version = "v1.0"
		)
)
public class BankReconciliationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankReconciliationServiceApplication.class, args);
	}

}
