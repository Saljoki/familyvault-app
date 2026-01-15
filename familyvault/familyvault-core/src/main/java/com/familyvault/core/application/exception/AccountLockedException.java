package com.familyvault.core.application.exception;

import java.time.Instant;

public class AccountLockedException extends ApplicationException {
    private final Instant lockedUntil;

    public AccountLockedException(Instant lockedUntil) {
        super("Account is locked until " + lockedUntil, "ACCOUNT_LOCKED");
        this.lockedUntil = lockedUntil;
    }

    public Instant getLockedUntil() {
        return lockedUntil;
    }
}
