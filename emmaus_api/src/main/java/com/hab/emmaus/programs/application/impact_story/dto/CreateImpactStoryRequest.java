package com.hab.emmaus.programs.application.impact_story.dto;

import java.util.UUID;

public record CreateImpactStoryRequest(
        UUID programId,
        String beneficiaryName,
        Integer age,
        Gender gender,
        String location,
        String imageUrl,
        String videoUrl,
        String shortQuote,
        String fullStory,
        String status,
        String remark,
        Integer displayOrder
) {}
