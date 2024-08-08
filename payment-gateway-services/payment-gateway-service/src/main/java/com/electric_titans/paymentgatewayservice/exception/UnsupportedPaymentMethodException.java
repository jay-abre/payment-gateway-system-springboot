package com.electric_titans.paymentgatewayservice.exception;

public class UnsupportedPaymentMethodException extends IllegalArgumentException {
    public UnsupportedPaymentMethodException(String message) {
        super(message);
    }
}