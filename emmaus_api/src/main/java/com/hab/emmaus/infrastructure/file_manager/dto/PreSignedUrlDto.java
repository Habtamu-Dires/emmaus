package com.hab.emmaus.infrastructure.file_manager.dto;

public record PreSignedUrlDto(
        String preSignedUrl,
        String publicUrl,
        String key
) {}
