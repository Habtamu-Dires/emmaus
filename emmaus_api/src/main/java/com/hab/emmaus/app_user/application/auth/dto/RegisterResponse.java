package com.hab.emmaus.app_user.application.auth.dto;

import java.util.UUID;

public record RegisterResponse(
   UUID userId,
   String phone
) {}
