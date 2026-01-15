package com.familyvault.core.application.exception;

public class InvalidCredentialsException extends ApplicationException {
    public InvalidCredentialsException() {
        super("Invalid email or password", "INVALID_CREDENTIALS");
    }
}
