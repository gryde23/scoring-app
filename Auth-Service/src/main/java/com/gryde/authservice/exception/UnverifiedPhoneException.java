package com.gryde.authservice.exception;

public class UnverifiedPhoneException extends RuntimeException {
    public UnverifiedPhoneException(String message) {
        super(message);
    }
}
