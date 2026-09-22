package com.hab.emmaus.app_user.event;

public record EmailResetEvent(
        String newEmail,
        String userName,
        String rawToken
) {
}
