package com.hab.emmaus.app_user.event;


public record PasswordResetEvent(
        String email,
        String userName,
        String rawToken
) {
}
