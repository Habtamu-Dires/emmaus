package com.hab.emmaus.programs.application.impact_story.dto;

import lombok.Builder;

import java.util.UUID;

@Builder
public record ImpactStoryResponse(
         UUID publicId,
         UUID programId,
         String program,
         String beneficiaryName,
         Integer age,
         String gender,
         String location,
         String imageUrl,
         String videoUrl,
         String shortQuote,
         String fullStory,
         String status,
         String remark
) {
}
