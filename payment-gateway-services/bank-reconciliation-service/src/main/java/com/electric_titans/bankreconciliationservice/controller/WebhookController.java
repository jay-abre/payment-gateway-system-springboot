package com.electric_titans.bankreconciliationservice.controller;

import com.electric_titans.bankreconciliationservice.config.StripeConfig;
import com.electric_titans.bankreconciliationservice.service.ReconciliationService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Controller
@RequestMapping("/api/reconciliations")
public class WebhookController {

    private static final Logger logger = LoggerFactory.getLogger(WebhookController.class);

    @Autowired
    private StripeConfig stripeConfig;

    @Autowired
    private ReconciliationService reconciliationService;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeConfig.getStripeApiKey();
    }

    @Operation(
            summary = "Handle Stripe Webhook Events",
            description = "Processes incoming Stripe webhook events to handle various payment-related events."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Webhook event handled successfully."),
            @ApiResponse(responseCode = "400", description = "Bad request due to invalid signature or payload.")
    })
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                @RequestHeader("Stripe-Signature") String sigHeader) {
        String endpointSecret = stripeConfig.getWebhookSecret();

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.toString());
        }

        switch (event.getType()) {
            case "payment_intent.succeeded":
                handlePaymentIntentSucceededEvent(event);
                break;
            case "payment_intent.failed":
                handlePaymentIntentFailedEvent(event);
                break;
            default:
                System.out.println("Unhandled event type: " + event.getType());
                break;
        }

        return ResponseEntity.ok("Success");
    }

    private void handlePaymentIntentSucceededEvent(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new RuntimeException("Failed to deserialize PaymentIntent from Stripe event"));

        logger.info("PaymentIntent Succeeded: {}", paymentIntent.getConfirmationMethod());

        reconciliationService.handlePaymentIntentSucceeded(paymentIntent);
    }

    private void handlePaymentIntentFailedEvent(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new RuntimeException("Failed to deserialize PaymentIntent from Stripe event"));

        reconciliationService.handlePaymentIntentFailed(paymentIntent);
    }
}