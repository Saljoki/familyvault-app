package com.familyvault.core.domain.model.family;

import java.util.UUID;

/**
 * Value object representing a unique family identifier.
 */
public record FamilyId(UUID value) {

    public FamilyId {
        if (value == null) {
            throw new IllegalArgumentException("FamilyId cannot be null");
        }
    }

    public static FamilyId generate() {
        return new FamilyId(UUID.randomUUID());
    }

    public static FamilyId of(UUID value) {
        return new FamilyId(value);
    }

    public static FamilyId of(String value) {
        return new FamilyId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
