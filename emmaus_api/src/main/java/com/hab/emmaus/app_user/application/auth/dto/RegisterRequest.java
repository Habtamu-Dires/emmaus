package com.hab.emmaus.app_user.application.auth.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotEmpty(message = "First Name is Mandatory")
        String firstName,
        @NotEmpty(message = "Last Name is Mandatory")
        String lastName,
        @NotEmpty(message = "Phone is Mandatory")
        @Pattern(
                regexp = "^(09|07)\\d{8}$",
                message = "Phone number must be 10 digits and start with 07 or 09"
        )
        String phoneNumber,
        String email,
        @Size(min = 6, max=8)
        @NotEmpty(message = "Password is Mandatory")
        String password,
        @Size(min = 6, max=8)
        @NotEmpty(message = "Confirm Password is Mandatory")
        String confirmPassword
) { }
