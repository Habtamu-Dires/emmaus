package com.hab.emmaus.app_user.application.app_user.dto;

import com.hab.emmaus.app_user.domain.app_user.enums.UserStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record RegisteredUsers(
        UUID publicId,
        String firstName,
        String lastName,
        String phone,
        String nationalId,
        UserStatus status,
        Instant createdAt
) {}
