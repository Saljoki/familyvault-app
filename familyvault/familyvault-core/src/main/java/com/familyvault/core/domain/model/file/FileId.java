package com.familyvault.core.domain.model.file;

import java.util.UUID;

/**
 * Value object representing a unique file identifier.
 */
public record FileId(UUID value) {

    public FileId {
        if (value == null) {
            throw new IllegalArgumentException("FileId cannot be null");
        }
    }

    public static FileId generate() {
        return new FileId(UUID.randomUUID());
    }

    public static FileId of(UUID value) {
        return new FileId(value);
    }

    public static FileId of(String value) {
        return new FileId(UUID.fromString(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
