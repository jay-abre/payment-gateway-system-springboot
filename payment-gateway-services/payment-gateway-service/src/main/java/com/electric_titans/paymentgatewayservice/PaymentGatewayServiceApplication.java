package com.electric_titans.paymentgatewayservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Payment Gateway Service REST API Documentation",
				version = "v1.0"
		)
)
public class PaymentGatewayServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentGatewayServiceApplication.class, args);
	}

}
