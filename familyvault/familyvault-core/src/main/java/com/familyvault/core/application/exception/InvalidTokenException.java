package com.familyvault.core.application.exception;

public class InvalidTokenException extends ApplicationException {
    public InvalidTokenException() {
        super("Invalid or expired token", "INVALID_TOKEN");
    }

    public InvalidTokenException(String message) {
        super(message, "INVALID_TOKEN");
    }
}
