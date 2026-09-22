package com.hab.emmaus.app_user.application.auth.dto;


import com.hab.emmaus.app_user.domain.app_user.enums.UserStatus;

public record AuthTokens(
        String accessToken,
        String refreshToken,
        UserStatus status,
        Boolean isTempPassword
) {
}
