package com.hab.emmaus.app_user.application.app_user.dto;

public record UpdateUserProfileRequest(
        String firstName,
        String lastName,
        String profilePic
) {
}
