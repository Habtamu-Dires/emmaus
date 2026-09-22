package com.hab.emmaus.app_user.application.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record OtpRequest(
        @NotEmpty(message = "Email is Mandatory")
        @Email(message = "Email is not valid")
        String email
) {
}
