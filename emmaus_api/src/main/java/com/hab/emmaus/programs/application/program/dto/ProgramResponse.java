package com.hab.emmaus.programs.application.program.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ProgramResponse(
        UUID publicId,
        String name,
        String description,
        Integer metricNumber,
        String metricLabel,
        String logoUrl,
        String status,
        Integer displayOrder
) {

}
