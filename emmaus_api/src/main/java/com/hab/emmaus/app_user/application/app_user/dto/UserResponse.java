package com.hab.emmaus.app_user.application.app_user.dto;

import lombok.Builder;

import java.util.UUID;
@Builder
public record UserResponse(
        UUID publicId,
        String name,
        String phone,
        String role,
        String discipline
) {
}
