package com.familyvault.core.domain.model.file;

import com.familyvault.core.domain.model.family.FamilyId;
import com.familyvault.core.domain.model.user.UserId;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Represents a stored file in the system.
 * Actual file content is in cloud storage; this holds metadata.
 */
@Getter
@Builder(toBuilder = true)
public class StoredFile {

    private final FileId id;
    private final FamilyId familyId;
    private final UUID folderId; // null = root level
    private final UserId uploadedBy;

    // File info
    private final String originalName;
    private final String storageKey; // Key in R2/S3

    // Type and size
    private final String mimeType;
    private final long fileSize;
    private final FileType fileType;

    // Media-specific
    private final String thumbnailKey;
    private final Integer width;
    private final Integer height;
    private final Integer durationSeconds;

    // Metadata (EXIF, etc.)
    private final Map<String, Object> metadata;

    // Status
    private final FileStatus status;

    private final Instant createdAt;
    private final Instant updatedAt;

    public enum FileStatus {
        PROCESSING,
        ACTIVE,
        FAILED
    }

    /**
     * Creates a new file record (processing state).
     */
    public static StoredFile create(
            FamilyId familyId,
            UUID folderId,
            UserId uploadedBy,
            String originalName,
            String storageKey,
            String mimeType,
            long fileSize
    ) {
        Instant now = Instant.now();
        return StoredFile.builder()
                .id(FileId.generate())
                .familyId(familyId)
                .folderId(folderId)
                .uploadedBy(uploadedBy)
                .originalName(originalName)
                .storageKey(storageKey)
                .mimeType(mimeType)
                .fileSize(fileSize)
                .fileType(FileType.fromMimeType(mimeType))
                .status(FileStatus.PROCESSING)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * Marks file as processed with optional media metadata.
     */
    public StoredFile markProcessed(String thumbnailKey, Integer width, Integer height, Integer durationSeconds) {
        return this.toBuilder()
                .status(FileStatus.ACTIVE)
                .thumbnailKey(thumbnailKey)
                .width(width)
                .height(height)
                .durationSeconds(durationSeconds)
                .updatedAt(Instant.now())
                .build();
    }

    public StoredFile markFailed() {
        return this.toBuilder()
                .status(FileStatus.FAILED)
                .updatedAt(Instant.now())
                .build();
    }

    public StoredFile moveTo(UUID newFolderId) {
        return this.toBuilder()
                .folderId(newFolderId)
                .updatedAt(Instant.now())
                .build();
    }

    public StoredFile rename(String newName) {
        return this.toBuilder()
                .originalName(newName)
                .updatedAt(Instant.now())
                .build();
    }

    public boolean isImage() {
        return fileType == FileType.IMAGE;
    }

    public boolean isVideo() {
        return fileType == FileType.VIDEO;
    }

    public boolean isMedia() {
        return fileType.isMedia();
    }

    public boolean isActive() {
        return status == FileStatus.ACTIVE;
    }

    public String getFileExtension() {
        int lastDot = originalName.lastIndexOf('.');
        return lastDot > 0 ? originalName.substring(lastDot + 1).toLowerCase() : "";
    }
}
