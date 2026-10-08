package com.hab.emmaus.programs.application.program.dto;

public record CreateProgramRequest(
        String name,
        String description,
        Integer metricNumber,
        String metricLabel,
        String logoUrl,
        String status,
        Integer displayOrder
) {}
