package com.hab.emmaus.app_user.application.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record ResetEmailRequest(
        @NotEmpty(message = "OTP Code is Mandatory")
        String rawToken,
        @Email(message = "Email not valid")
        @NotEmpty(message = "New Email is Mandatory")
        String newEmail
) {
}
