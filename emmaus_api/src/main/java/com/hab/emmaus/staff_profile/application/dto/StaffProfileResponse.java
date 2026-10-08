package com.hab.emmaus.staff_profile.application.dto;

import com.hab.emmaus.staff_profile.domain.StaffProfile;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record StaffProfileResponse(
        UUID publicId,
        String firstName,
        String lastName,
        String email,
        String phone,
        String position,
        String department,
        String description,
        String bio,
        String profilePic,
        String linkedinUrl,
        Integer displayOrder,
        Instant createdAt
) {}
