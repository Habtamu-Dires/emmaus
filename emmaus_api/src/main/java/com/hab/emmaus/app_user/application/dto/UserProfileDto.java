package com.hab.emmaus.app_user.application.dto;


import com.hab.emmaus.app_user.domain.app_user.enums.UserStatus;

import java.util.UUID;

public record UserProfileDto(
        UUID publicId,
        String phone,
        String firstName,
        String lastName,
        String fullName,
        UserStatus status
) {
}
