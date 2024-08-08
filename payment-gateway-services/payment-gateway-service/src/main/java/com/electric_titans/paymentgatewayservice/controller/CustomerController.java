package com.electric_titans.paymentgatewayservice.controller;

import com.electric_titans.paymentgatewayservice.service.CustomerService;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/stripe")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;


    @Operation(
            summary = "List All Customers",
            description = "Retrieves a list of all customers from the database."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved customer list"),
            @ApiResponse(responseCode = "500", description = "An error occurred while listing customers")
    })
    @GetMapping("/list-customer")
    public ResponseEntity<?> listAllCustomers() {
        logger.info("Listing all customers");
        try {
            CustomerCollection customers = customerService.listAllCustomers();
            logger.info("Successfully retrieved customer list");
            return ResponseEntity.ok().body(customers.getData());
        } catch (Exception e) {
            logger.error("Error listing customers", e);
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Get Customer by ID",
            description = "Retrieves a customer by their ID from the database."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved customer details"),
            @ApiResponse(responseCode = "400", description = "Customer not found with the given ID"),
            @ApiResponse(responseCode = "500", description = "An error occurred while retrieving customer")
    })
    @GetMapping("/get-customer/{customerId}")
    public ResponseEntity<?> getCustomer(@PathVariable String customerId) {
        logger.info("Attempting to retrieve customer with ID: {}", customerId);
        try {
            Customer customer = customerService.getCustomer(customerId);
            if (customer != null) {
                logger.info("Successfully retrieved customer with ID: {}", customerId);
                return ResponseEntity.ok().body(Map.of(
                        "Name", customer.getName(),
                        "Email", customer.getEmail(),
                        "customerId", customer.getId()
                ));
            } else {
                logger.error("Customer not found with ID: {}", customerId);
                return ResponseEntity.badRequest().body("Customer not found");
            }
        } catch (Exception e) {
            logger.error("Error retrieving customer with ID: {}", customerId, e);
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }

    @Operation(
            summary = "Delete Customer by ID",
            description = "Deletes a customer from the database by their ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer deleted successfully"),
            @ApiResponse(responseCode = "500", description = "An error occurred while deleting the customer")
    })
    @DeleteMapping("/delete-customer/{customerId}")
    public ResponseEntity<?> deleteCustomer(@PathVariable String customerId) {
        logger.info("Attempting to delete customer with ID: {}", customerId);
        try {
            customerService.deleteCustomer(customerId);
            logger.info("Customer deleted successfully with ID: {}", customerId);
            return ResponseEntity.ok().body("Customer deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting customer with ID: {}", customerId, e);
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }
}