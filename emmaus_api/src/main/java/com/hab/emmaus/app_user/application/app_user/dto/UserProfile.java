package com.hab.emmaus.app_user.application.app_user.dto;


import com.hab.emmaus.app_user.domain.app_user.AppUser;
import com.hab.emmaus.app_user.domain.app_user.enums.UserStatus;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;


@Builder
public record UserProfile(
        @NotNull(message = "Public id is required")
        UUID publicId,
        @NotEmpty(message = "Email id is required")
        String email,
        @NotEmpty(message = "FirstName id is required")
        String firstName,
        @NotEmpty(message = "LastName id is required")
        String lastName,
        String profilePic,
        @NotNull(message = "Status is required")
        UserStatus status,
        Boolean isEmailVerified,
        String remark,
        Instant createdAt,
        Instant updatedAt
) {

    public static UserProfile from(AppUser appUser){
        return UserProfile.builder()
                .publicId(appUser.getPublicId())
                .firstName(appUser.getFirstName())
                .lastName(appUser.getLastName())
                .email(appUser.getEmail())
                .profilePic(appUser.getProfilePic())
                .status(appUser.getStatus())
                .isEmailVerified(appUser.getIsEmailVerified())
                .remark(appUser.getRemark())
                .createdAt(appUser.getCreatedAt())
                .updatedAt(appUser.getUpdatedAt())
                .build();
    }
}
