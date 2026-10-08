package com.hab.emmaus.document.application.dto;

import java.time.LocalDate;

public record CreateDocumentRequest(
        String documentType,
        String title,
        String description,
        String url,
        String thumbnailUrl,
        LocalDate publishedDate,
        Integer displayOrder
) {}
