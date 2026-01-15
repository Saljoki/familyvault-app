package com.familyvault.core.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

/**
 * Request for initiating a file upload.
 */
public record InitiateUploadRequest(
        @NotNull(message = "Family ID is required")
        UUID familyId,

        UUID folderId, // null for root

        @NotBlank(message = "File name is required")
        String fileName,

        @NotBlank(message = "Content type is required")
        String contentType,

        @Positive(message = "File size must be positive")
        long fileSize
) {
}
