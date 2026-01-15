package com.familyvault.core.application.exception;

public class StorageQuotaExceededException extends ApplicationException {
    private final long availableBytes;
    private final long requestedBytes;

    public StorageQuotaExceededException(long availableBytes, long requestedBytes) {
        super(String.format("Storage quota exceeded. Available: %d bytes, Requested: %d bytes",
                availableBytes, requestedBytes), "STORAGE_QUOTA_EXCEEDED");
        this.availableBytes = availableBytes;
        this.requestedBytes = requestedBytes;
    }

    public long getAvailableBytes() {
        return availableBytes;
    }

    public long getRequestedBytes() {
        return requestedBytes;
    }
}
