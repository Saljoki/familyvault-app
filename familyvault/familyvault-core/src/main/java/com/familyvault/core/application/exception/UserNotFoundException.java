package com.familyvault.core.application.exception;

import com.familyvault.core.domain.model.user.UserId;

public class UserNotFoundException extends ApplicationException {
    public UserNotFoundException(UserId id) {
        super("User not found: " + id, "USER_NOT_FOUND");
    }

    public UserNotFoundException(String email) {
        super("User not found with email: " + email, "USER_NOT_FOUND");
    }
}
