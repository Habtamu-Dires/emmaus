package com.hab.emmaus.programs.application.impact_story.dto;

import lombok.Builder;

@Builder
public record ImpactStatisticsResponse(
        Integer totalImpacts,
        Integer childImpacts,
        Integer womenImpacts,
        Integer elderlyImpacts,
        Integer specialNeedsImpacts
) {}
