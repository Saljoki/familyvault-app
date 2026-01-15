package com.familyvault.core.application.dto.response;

import java.util.UUID;

/**
 * Response containing presigned URL for file upload.
 */
public record UploadUrlResponse(
        UUID fileId,
        String uploadUrl,
        String storageKey,
        long expiresInSeconds
) {
}
