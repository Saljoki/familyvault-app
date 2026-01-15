package com.familyvault.core.application.exception;

import java.util.UUID;

public class FileNotFoundException extends ApplicationException {
    public FileNotFoundException(UUID fileId) {
        super("File not found: " + fileId, "FILE_NOT_FOUND");
    }
}
