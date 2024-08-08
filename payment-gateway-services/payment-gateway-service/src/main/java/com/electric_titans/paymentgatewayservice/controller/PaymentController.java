package com.electric_titans.paymentgatewayservice.controller;

import com.electric_titans.common.event.TransferCreatedEvent;
import com.electric_titans.paymentgatewayservice.entity.Payment;
import com.electric_titans.paymentgatewayservice.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.Balance;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Payout;
import com.stripe.model.Transfer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/stripe")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;



//    @PostMapping("/deposit")
//    public ResponseEntity<?> deposit(@RequestBody DepositEvent depositRequest) {
//        try {
//            PaymentIntent paymentIntent = paymentService.createDeposit(depositRequest);
//            return ResponseEntity.ok().body(Map.of(
//                    "message", "Deposit created successfully",
//                    "paymentIntentId", paymentIntent.getId(),
//                    "paymentMethodId", paymentIntent.getPaymentMethod(),
//                    "amount", paymentIntent.getAmount(),
//                    "currency", paymentIntent.getCurrency(),
//                    "status", paymentIntent.getStatus()
//            ));
//        } catch (StripeException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create deposit: " + e.getMessage());
//        }
//    }


    @Operation(
            summary = "Confirm a Payment",
            description = "Confirms a payment using the Stripe API and returns details of the payment intent."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment confirmed successfully")
    })
    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(@RequestBody Payment payment) {
        try {
            PaymentIntent paymentIntent = paymentService.confirmPayment(payment);
            return ResponseEntity.ok().body(Map.of(
                    "message", "Payment confirmed successfully",
                    "paymentIntentId", paymentIntent.getId(),
                    "amount", paymentIntent.getAmount(),
                    "currency", paymentIntent.getCurrency(),
                    "status", paymentIntent.getStatus()
            ));
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to confirm payment: " + e.getMessage());
        }
    }


//    @PostMapping("/transfer")
//    public ResponseEntity<?> transfer(@RequestBody TransferCreatedEvent transferRequest) {
//        try {
//            Transfer transfer = paymentService.createTransfer(transferRequest);
//            return ResponseEntity.ok().body(Map.of(
//                    "message", "Transfer created successfully",
//                    "transferId", transfer.getId(),
//                    "amount", transfer.getAmount(),
//                    "currency", transfer.getCurrency()
//            ));
//        } catch (StripeException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create transfer: " + e.getMessage());
//        }
//    }
//    @GetMapping("/balance")
//    public ResponseEntity<?> getBalance() {
//        try {
//            Balance balanceService = paymentService.getBalance();
//            return ResponseEntity.ok().body(Map.of(
//                    "message", "Balance retrieved successfully",
//                    "pending", balanceService.getPending().get(0).getAmount()
//            ));
//        } catch (StripeException e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get balance: " + e.getMessage());
//        }
//    }

}
