package com.hab.emmaus.app_user.application.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record ChangePasswordRequest(
        @NotEmpty(message = "Email is Mandatory")
        @Email(message = "Email is not valid")
        String email,
        @NotEmpty(message = "Current Password is Mandatory")
        String currentPassword,
        @NotEmpty(message = "New Password is Mandatory")
        String newPassword,
        @NotEmpty(message = "Confirm Password is Mandatory")
        String confirmPassword
) {
}
