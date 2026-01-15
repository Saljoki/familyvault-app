package com.familyvault.core.application.port.out.storage;

import java.io.InputStream;
import java.time.Duration;
import java.util.Optional;

/**
 * Port for cloud storage operations.
 * Implemented by S3/R2/etc adapter.
 */
public interface FileStoragePort {

    /**
     * Generate a presigned URL for uploading a file.
     *
     * @param storageKey  The key (path) in storage
     * @param contentType MIME type of the file
     * @param expiration  How long the URL is valid
     * @return Presigned URL for PUT request
     */
    String generateUploadUrl(String storageKey, String contentType, Duration expiration);

    /**
     * Generate a presigned URL for downloading a file.
     *
     * @param storageKey The key (path) in storage
     * @param expiration How long the URL is valid
     * @return Presigned URL for GET request
     */
    String generateDownloadUrl(String storageKey, Duration expiration);

    /**
     * Generate a presigned URL for viewing (inline) a file.
     *
     * @param storageKey  The key (path) in storage
     * @param contentType MIME type for Content-Type header
     * @param expiration  How long the URL is valid
     * @return Presigned URL for inline viewing
     */
    String generateViewUrl(String storageKey, String contentType, Duration expiration);

    /**
     * Upload a file directly (for server-side operations like thumbnails).
     *
     * @param storageKey  The key (path) in storage
     * @param content     File content stream
     * @param contentType MIME type
     * @param size        File size in bytes
     */
    void upload(String storageKey, InputStream content, String contentType, long size);

    /**
     * Download a file (for server-side processing).
     *
     * @param storageKey The key (path) in storage
     * @return File content stream
     */
    Optional<InputStream> download(String storageKey);

    /**
     * Delete a file from storage.
     *
     * @param storageKey The key (path) in storage
     */
    void delete(String storageKey);

    /**
     * Check if a file exists.
     *
     * @param storageKey The key (path) in storage
     * @return true if exists
     */
    boolean exists(String storageKey);

    /**
     * Copy a file within storage.
     *
     * @param sourceKey      Source key
     * @param destinationKey Destination key
     */
    void copy(String sourceKey, String destinationKey);
}
