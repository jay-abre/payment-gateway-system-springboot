package com.electric_titans.accountservice.exception;

public class KycNotFullyVerifiedException extends RuntimeException {

    public KycNotFullyVerifiedException(Long userId) {
        super(String.format("Kyc not fully verified for user with id : %s", userId));
    }
}
