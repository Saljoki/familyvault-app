package com.familyvault.core.domain.model.file;

import java.util.Set;

/**
 * Types of files supported by the system.
 */
public enum FileType {
    IMAGE(Set.of("image/jpeg", "image/png", "image/gif", "image/webp", "image/heic", "image/heif")),
    VIDEO(Set.of("video/mp4", "video/quicktime", "video/x-msvideo", "video/webm", "video/x-matroska")),
    DOCUMENT(Set.of("application/pdf", "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/plain")),
    AUDIO(Set.of("audio/mpeg", "audio/wav", "audio/ogg", "audio/aac", "audio/flac")),
    OTHER(Set.of());

    private final Set<String> mimeTypes;

    FileType(Set<String> mimeTypes) {
        this.mimeTypes = mimeTypes;
    }

    public Set<String> getMimeTypes() {
        return mimeTypes;
    }

    public static FileType fromMimeType(String mimeType) {
        if (mimeType == null) {
            return OTHER;
        }

        String lowerMime = mimeType.toLowerCase();

        for (FileType type : values()) {
            if (type.mimeTypes.contains(lowerMime)) {
                return type;
            }
        }

        // Fallback based on mime type prefix
        if (lowerMime.startsWith("image/")) return IMAGE;
        if (lowerMime.startsWith("video/")) return VIDEO;
        if (lowerMime.startsWith("audio/")) return AUDIO;

        return OTHER;
    }

    public boolean isMedia() {
        return this == IMAGE || this == VIDEO;
    }
}
