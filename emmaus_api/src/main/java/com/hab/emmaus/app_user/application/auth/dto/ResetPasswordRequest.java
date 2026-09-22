package com.hab.emmaus.app_user.application.auth.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotEmpty(message = "OTP Code is Mandatory")
        String rawToken,
        @Size(min = 6, max=8)
        @NotEmpty(message = "New Password is Mandatory")
        String newPassword,
        @Size(min = 6, max=8)
        @NotEmpty(message = "Confirm Password is Mandatory")
        String confirmPassword
) {
}
