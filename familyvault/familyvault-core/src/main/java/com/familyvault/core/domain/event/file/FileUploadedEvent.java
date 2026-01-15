package com.familyvault.core.domain.event.file;

import com.familyvault.core.domain.event.DomainEvent;
import com.familyvault.core.domain.model.file.FileType;

import java.time.Instant;
import java.util.UUID;

/**
 * Event raised when a file is uploaded.
 */
public record FileUploadedEvent(
        UUID eventId,
        Instant occurredAt,
        UUID fileId,
        UUID familyId,
        UUID uploadedBy,
        String fileName,
        FileType fileType,
        long fileSize
) implements DomainEvent {

    public static FileUploadedEvent create(
            UUID fileId,
            UUID familyId,
            UUID uploadedBy,
            String fileName,
            FileType fileType,
            long fileSize
    ) {
        return new FileUploadedEvent(
                UUID.randomUUID(),
                Instant.now(),
                fileId,
                familyId,
                uploadedBy,
                fileName,
                fileType,
                fileSize
        );
    }

    @Override
    public String getEventType() {
        return "FILE_UPLOADED";
    }

    @Override
    public UUID getAggregateId() {
        return fileId;
    }

    @Override
    public String getAggregateType() {
        return "File";
    }
}
