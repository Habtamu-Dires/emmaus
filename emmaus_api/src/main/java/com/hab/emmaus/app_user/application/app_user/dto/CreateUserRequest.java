package com.hab.emmaus.app_user.application.app_user.dto;


import com.hab.emmaus.app_user.domain.app_user.enums.UserRole;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        @NotEmpty(message = "FirstName number is mandatory")
        String firstName,
        @NotEmpty(message = "LastName number is mandatory")
        String lastName,
        @NotEmpty(message = "Phone number is mandatory")
        @Nullable
        @Email(message = "Email is not valid")
        String email,
        @NotNull(message = "Role is mandatory")
        UserRole role,
        String remark
) {}
