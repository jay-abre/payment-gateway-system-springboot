package com.electric_titans.transactionservice;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Transaction Service REST API Documentation",
				version = "v1.0"
		)
)
@EnableScheduling
public class TransactionServiceApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(TransactionServiceApplication.class, args);
	}
}
