package com.familyvault.api.controller;

import com.familyvault.api.security.CurrentUser;
import com.familyvault.core.application.dto.request.InitiateUploadRequest;
import com.familyvault.core.application.dto.response.FileResponse;
import com.familyvault.core.application.dto.response.UploadUrlResponse;
import com.familyvault.core.application.service.file.FileService;
import com.familyvault.core.domain.model.file.FileType;
import com.familyvault.core.domain.model.user.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Tag(name = "Files", description = "File upload and management")
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload/initiate")
    @Operation(summary = "Initiate file upload", description = "Get a presigned URL for uploading a file")
    public ResponseEntity<UploadUrlResponse> initiateUpload(
            @Valid @RequestBody InitiateUploadRequest request,
            @CurrentUser UserId currentUser
    ) {
        UploadUrlResponse response = fileService.initiateUpload(request, currentUser);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload/{fileId}/confirm")
    @Operation(summary = "Confirm upload", description = "Confirm that file upload is complete")
    public ResponseEntity<FileResponse> confirmUpload(
            @PathVariable UUID fileId,
            @CurrentUser UserId currentUser
    ) {
        FileResponse response = fileService.confirmUpload(fileId, currentUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{fileId}/download-url")
    @Operation(summary = "Get download URL", description = "Get a presigned URL for downloading a file")
    public ResponseEntity<Map<String, String>> getDownloadUrl(
            @PathVariable UUID fileId,
            @CurrentUser UserId currentUser
    ) {
        String url = fileService.getDownloadUrl(fileId, currentUser);
        return ResponseEntity.ok(Map.of("url", url));
    }

    @GetMapping("/{fileId}/view-url")
    @Operation(summary = "Get view URL", description = "Get a presigned URL for viewing a file inline")
    public ResponseEntity<Map<String, String>> getViewUrl(
            @PathVariable UUID fileId,
            @CurrentUser UserId currentUser
    ) {
        String url = fileService.getViewUrl(fileId, currentUser);
        return ResponseEntity.ok(Map.of("url", url));
    }

    @GetMapping
    @Operation(summary = "List files", description = "List files in a family with optional filters")
    public ResponseEntity<List<FileResponse>> listFiles(
            @RequestParam UUID familyId,
            @RequestParam(required = false) UUID folderId,
            @RequestParam(required = false) FileType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @CurrentUser UserId currentUser
    ) {
        List<FileResponse> files = fileService.listFiles(familyId, folderId, type, page, size, currentUser);
        return ResponseEntity.ok(files);
    }

    @DeleteMapping("/{fileId}")
    @Operation(summary = "Delete file", description = "Delete a file")
    public ResponseEntity<Void> deleteFile(
            @PathVariable UUID fileId,
            @CurrentUser UserId currentUser
    ) {
        fileService.deleteFile(fileId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
