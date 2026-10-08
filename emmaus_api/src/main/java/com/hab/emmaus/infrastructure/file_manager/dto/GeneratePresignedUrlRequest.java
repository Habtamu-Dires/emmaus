package com.hab.emmaus.infrastructure.file_manager.dto;

import jakarta.validation.constraints.NotEmpty;

public record GeneratePresignedUrlRequest(
        @NotEmpty(message = "FileName is required")
        String fileName,
        @NotEmpty(message =  "Folder Name is required")
        String folder,
        @NotEmpty(message = "Content type is required")
        String contentType
) {
}
