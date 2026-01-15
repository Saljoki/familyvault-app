package com.familyvault.core.application.service.file;

import com.familyvault.core.application.dto.request.InitiateUploadRequest;
import com.familyvault.core.application.dto.response.FileResponse;
import com.familyvault.core.application.dto.response.UploadUrlResponse;
import com.familyvault.core.application.exception.FamilyNotFoundException;
import com.familyvault.core.application.exception.FileNotFoundException;
import com.familyvault.core.application.exception.StorageQuotaExceededException;
import com.familyvault.core.application.exception.UnauthorizedException;
import com.familyvault.core.application.port.out.persistence.FamilyRepository;
import com.familyvault.core.application.port.out.persistence.FileRepository;
import com.familyvault.core.application.port.out.storage.FileStoragePort;
import com.familyvault.core.domain.model.family.Family;
import com.familyvault.core.domain.model.family.FamilyId;
import com.familyvault.core.domain.model.family.FamilyMember;
import com.familyvault.core.domain.model.file.FileId;
import com.familyvault.core.domain.model.file.FileType;
import com.familyvault.core.domain.model.file.StoredFile;
import com.familyvault.core.domain.model.user.UserId;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * Application service handling file operations.
 */
@RequiredArgsConstructor
public class FileService {

    private static final Duration UPLOAD_URL_EXPIRATION = Duration.ofMinutes(15);
    private static final Duration DOWNLOAD_URL_EXPIRATION = Duration.ofHours(1);
    private static final Duration THUMBNAIL_URL_EXPIRATION = Duration.ofHours(24);

    private final FileRepository fileRepository;
    private final FamilyRepository familyRepository;
    private final FileStoragePort fileStorage;

    /**
     * Initiate a file upload by generating a presigned URL.
     */
    public UploadUrlResponse initiateUpload(InitiateUploadRequest request, UserId uploaderId) {
        FamilyId familyId = FamilyId.of(request.familyId());

        // Verify user is a member
        FamilyMember member = familyRepository.findMember(familyId, uploaderId)
                .orElseThrow(() -> new UnauthorizedException("Not a member of this family"));

        if (!member.canUploadFiles()) {
            throw new UnauthorizedException("Not authorized to upload files");
        }

        // Check storage quota
        Family family = familyRepository.findById(familyId)
                .orElseThrow(() -> new FamilyNotFoundException(familyId));

        if (!family.hasStorageAvailable(request.fileSize())) {
            throw new StorageQuotaExceededException(
                    family.getStorageAvailableBytes(),
                    request.fileSize()
            );
        }

        // Generate storage key
        String storageKey = generateStorageKey(familyId, request.fileName());

        // Create file record (PROCESSING state)
        StoredFile file = StoredFile.create(
                familyId,
                request.folderId(),
                uploaderId,
                request.fileName(),
                storageKey,
                request.contentType(),
                request.fileSize()
        );
        file = fileRepository.saveFile(file);

        // Generate presigned upload URL
        String uploadUrl = fileStorage.generateUploadUrl(
                storageKey,
                request.contentType(),
                UPLOAD_URL_EXPIRATION
        );

        return new UploadUrlResponse(
                file.getId().value(),
                uploadUrl,
                storageKey,
                UPLOAD_URL_EXPIRATION.toSeconds()
        );
    }

    /**
     * Confirm upload completion and process the file.
     */
    public FileResponse confirmUpload(UUID fileId, UserId userId) {
        StoredFile file = fileRepository.findFileById(FileId.of(fileId))
                .orElseThrow(() -> new FileNotFoundException(fileId));

        // Verify ownership
        if (!file.getUploadedBy().equals(userId)) {
            throw new UnauthorizedException("Not authorized to confirm this upload");
        }

        // Verify file exists in storage
        if (!fileStorage.exists(file.getStorageKey())) {
            file = file.markFailed();
            fileRepository.saveFile(file);
            throw new FileNotFoundException(fileId);
        }

        // Update family storage usage
        Family family = familyRepository.findById(file.getFamilyId())
                .orElseThrow(() -> new FamilyNotFoundException(file.getFamilyId()));
        family = family.addStorageUsed(file.getFileSize());
        familyRepository.save(family);

        // Mark as active (thumbnail generation would be async in production)
        String thumbnailKey = null;
        if (file.isImage()) {
            thumbnailKey = generateThumbnailKey(file.getStorageKey());
            // TODO: Async thumbnail generation
        }

        file = file.markProcessed(thumbnailKey, null, null, null);
        file = fileRepository.saveFile(file);

        String thumbnailUrl = thumbnailKey != null
                ? fileStorage.generateViewUrl(thumbnailKey, "image/jpeg", THUMBNAIL_URL_EXPIRATION)
                : null;

        return FileResponse.from(file, thumbnailUrl);
    }

    /**
     * Get a presigned URL for downloading a file.
     */
    public String getDownloadUrl(UUID fileId, UserId userId) {
        StoredFile file = fileRepository.findFileById(FileId.of(fileId))
                .orElseThrow(() -> new FileNotFoundException(fileId));

        // Verify user is a family member
        if (!familyRepository.isMember(file.getFamilyId(), userId)) {
            throw new UnauthorizedException("Not authorized to access this file");
        }

        return fileStorage.generateDownloadUrl(file.getStorageKey(), DOWNLOAD_URL_EXPIRATION);
    }

    /**
     * Get a presigned URL for viewing a file (inline).
     */
    public String getViewUrl(UUID fileId, UserId userId) {
        StoredFile file = fileRepository.findFileById(FileId.of(fileId))
                .orElseThrow(() -> new FileNotFoundException(fileId));

        if (!familyRepository.isMember(file.getFamilyId(), userId)) {
            throw new UnauthorizedException("Not authorized to access this file");
        }

        return fileStorage.generateViewUrl(
                file.getStorageKey(),
                file.getMimeType(),
                DOWNLOAD_URL_EXPIRATION
        );
    }

    /**
     * List files in a family/folder.
     */
    public List<FileResponse> listFiles(UUID familyId, UUID folderId, FileType type, int page, int size, UserId userId) {
        FamilyId famId = FamilyId.of(familyId);

        if (!familyRepository.isMember(famId, userId)) {
            throw new UnauthorizedException("Not authorized to access this family");
        }

        List<StoredFile> files = fileRepository.findFiles(famId, folderId, type, page, size);

        return files.stream()
                .map(file -> {
                    String thumbnailUrl = file.getThumbnailKey() != null
                            ? fileStorage.generateViewUrl(file.getThumbnailKey(), "image/jpeg", THUMBNAIL_URL_EXPIRATION)
                            : null;
                    return FileResponse.from(file, thumbnailUrl);
                })
                .toList();
    }

    /**
     * Delete a file.
     */
    public void deleteFile(UUID fileId, UserId userId) {
        StoredFile file = fileRepository.findFileById(FileId.of(fileId))
                .orElseThrow(() -> new FileNotFoundException(fileId));

        // Check permissions
        FamilyMember member = familyRepository.findMember(file.getFamilyId(), userId)
                .orElseThrow(() -> new UnauthorizedException("Not a member of this family"));

        // Only uploader or admin can delete
        boolean isUploader = file.getUploadedBy().equals(userId);
        if (!isUploader && !member.canDeleteFiles()) {
            throw new UnauthorizedException("Not authorized to delete this file");
        }

        // Delete from storage
        fileStorage.delete(file.getStorageKey());
        if (file.getThumbnailKey() != null) {
            fileStorage.delete(file.getThumbnailKey());
        }

        // Update family storage usage
        Family family = familyRepository.findById(file.getFamilyId())
                .orElseThrow(() -> new FamilyNotFoundException(file.getFamilyId()));
        family = family.removeStorageUsed(file.getFileSize());
        familyRepository.save(family);

        // Delete record
        fileRepository.deleteFile(file.getId());
    }

    private String generateStorageKey(FamilyId familyId, String fileName) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String sanitizedName = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        return String.format("families/%s/files/%s_%s_%s",
                familyId.value(), timestamp, uuid, sanitizedName);
    }

    private String generateThumbnailKey(String originalKey) {
        int lastDot = originalKey.lastIndexOf('.');
        String baseName = lastDot > 0 ? originalKey.substring(0, lastDot) : originalKey;
        return baseName + "_thumb.jpg";
    }
}
