package com.hab.emmaus.app_user.application.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record AuthenticationRequest(
        @NotEmpty(message = "Email is Mandatory")
        @Email(message = "Email is not valid")
        String email,
        @NotEmpty(message = "Password is Mandatory")
        String password
) { }
