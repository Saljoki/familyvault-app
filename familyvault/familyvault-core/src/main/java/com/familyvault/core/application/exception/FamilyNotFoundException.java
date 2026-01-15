package com.familyvault.core.application.exception;

import com.familyvault.core.domain.model.family.FamilyId;

import java.util.UUID;

public class FamilyNotFoundException extends ApplicationException {
    public FamilyNotFoundException(FamilyId id) {
        super("Family not found: " + id, "FAMILY_NOT_FOUND");
    }

    public FamilyNotFoundException(String inviteCode) {
        super("Family not found with invite code: " + inviteCode, "FAMILY_NOT_FOUND");
    }
}
