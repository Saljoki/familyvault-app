package com.familyvault.core.application.exception;

public class EmailAlreadyExistsException extends ApplicationException {
    public EmailAlreadyExistsException(String email) {
        super("Email already registered: " + email, "EMAIL_EXISTS");
    }
}
