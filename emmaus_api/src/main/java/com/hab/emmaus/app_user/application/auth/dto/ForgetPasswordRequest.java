package com.hab.emmaus.app_user.application.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public record ForgetPasswordRequest(

        @NotEmpty(message = "Email is Mandatory")
        @Email(message = "Email is not valid")
        String email
) {}
