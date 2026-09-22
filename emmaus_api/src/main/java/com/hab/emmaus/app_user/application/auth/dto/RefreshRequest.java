package com.hab.emmaus.app_user.application.auth.dto;

import lombok.Builder;


@Builder
public record RefreshRequest(
    String refreshToken
) { }
