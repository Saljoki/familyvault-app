package com.familyvault.core.application.dto.response;

import com.familyvault.core.domain.model.file.FileType;
import com.familyvault.core.domain.model.file.StoredFile;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for file information.
 */
public record FileResponse(
        UUID id,
        UUID familyId,
        UUID folderId,
        UUID uploadedBy,
        String originalName,
        String mimeType,
        long fileSize,
        FileType fileType,
        String thumbnailUrl,
        Integer width,
        Integer height,
        Integer durationSeconds,
        String status,
        Instant createdAt
) {

    public static FileResponse from(StoredFile file, String thumbnailUrl) {
        return new FileResponse(
                file.getId().value(),
                file.getFamilyId().value(),
                file.getFolderId(),
                file.getUploadedBy().value(),
                file.getOriginalName(),
                file.getMimeType(),
                file.getFileSize(),
                file.getFileType(),
                thumbnailUrl,
                file.getWidth(),
                file.getHeight(),
                file.getDurationSeconds(),
                file.getStatus().name(),
                file.getCreatedAt()
        );
    }
}
