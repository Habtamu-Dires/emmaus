package com.hab.emmaus.staff_profile.application.dto;

public record CreatStaffProfileRequest(
        String firstName,
        String lastName,
        String email,
        String phone,
        String position,
        String department,
        String description,
        String bio,
        String profilePic,
        String linkedinUrl,
        Integer displayOrder
) {
}
