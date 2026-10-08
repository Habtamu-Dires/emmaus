package com.hab.emmaus.document.application.dto;

import lombok.Builder;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record DocumentResponse(
        UUID publicId,
        String documentType,
        String title,
        String description,
        String url,
        String thumbnailUrl,
        LocalDate publishedDate,
        Integer displayOrder,
        Instant uploadedAt
) { }
