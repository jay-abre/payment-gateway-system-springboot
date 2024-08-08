package com.electric_titans.transactionservice.exception;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException(Long currentBalance, Long amount) {
        super(String.format("Attempted to withdraw %s, but the current balance is only %s.", amount, currentBalance));
    }
}
