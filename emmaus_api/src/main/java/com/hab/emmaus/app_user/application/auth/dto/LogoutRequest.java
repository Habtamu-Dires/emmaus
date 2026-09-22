package com.hab.emmaus.app_user.application.auth.dto;

public record LogoutRequest(
        String refreshToken
) {
}
